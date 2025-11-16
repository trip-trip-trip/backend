package yeohaenggasijo.tripshot.service.storage;


import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yeohaenggasijo.tripshot.domain.common.CaptureType;
import yeohaenggasijo.tripshot.domain.common.ContentType;
import yeohaenggasijo.tripshot.domain.common.MediaKind;
import yeohaenggasijo.tripshot.domain.common.ReelRenderStatus;
import yeohaenggasijo.tripshot.domain.media.MediaAsset;
import yeohaenggasijo.tripshot.domain.reel.ShortReel;
import yeohaenggasijo.tripshot.domain.reel.ShortReelItem;
import yeohaenggasijo.tripshot.repository.MediaAssetRepository;
import yeohaenggasijo.tripshot.repository.ShortReelItemRepository;
import yeohaenggasijo.tripshot.repository.ShortReelRepository;
import yeohaenggasijo.tripshot.service.OauthService;
import yeohaenggasijo.tripshot.util.DownloadUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RenderService {
    private final ShortReelRepository shortReelRepository;
    private final ShortReelItemRepository shortReelItemRepository;
    private final MediaAssetRepository mediaAssetRepository;
    private final StorageUploader storageUploader; // R2 업로더 (이미 쓰는 거 연결)

    private final DownloadUtil downloadUtil;
    private static final Logger logger = LoggerFactory.getLogger(RenderService.class);

    @Value("${media.ffmpegPath:ffmpeg}")
    private String ffmpegPath;

    @Async
    @Transactional
    public void renderAsync(Long reelId) throws IOException {

        Object EntityNotFoundException;
        ShortReel reel = shortReelRepository.findById(reelId).orElseThrow(jakarta.persistence.EntityNotFoundException::new);
        if (reel.getRenderStatus() == ReelRenderStatus.RENDERING) return;

        reel.setRenderStatus(ReelRenderStatus.RENDERING);
        shortReelRepository.save(reel);

        Path work = Files.createTempDirectory("reel_" + reelId + "_");
        try {
            // 1) 아이템 순서대로 3초 클립으로 트림
            List<ShortReelItem> items = shortReelItemRepository.findByReel_IdOrderByPositionAsc(reelId);
            List<Path> segments = new ArrayList<>();

            int idx = 0;
            for (ShortReelItem it : items) {
                MediaAsset m = it.getMedia();
                Path segOut = work.resolve(String.format("seg_%03d.mp4", idx++));
                // 다운로드 (URL → temp 파일), 이미 로컬이면 스킵
                Path in = downloadUtil.downloadToTemp(m.getUrl(), work); // 구현부는 네가 쓰는 HTTP/R2 클라로

                // -t 3 으로 앞 3초 추출, 스케일/프레임 통일
                List<String> cmd = List.of(
                        ffmpegPath, "-y",
                        "-i", in.toString(),
                        "-t", "3",
                        "-vf", "scale=1080:-2,fps=30",
                        "-an",
                        "-c:v", "libx264", "-preset", "veryfast", "-crf", "23",
                        segOut.toString()
                );
                run(cmd, work);
                segments.add(segOut);
            }

            if (segments.isEmpty()) {
                throw new IllegalStateException("No clips to render");
            }

            // 2) concat list 파일 작성
            Path list = work.resolve("list.txt");
            try (BufferedWriter w = Files.newBufferedWriter(list)) {
                for (Path p : segments) {
                    w.write("file '" + p.toAbsolutePath().toString().replace("'", "'\\''") + "'\n");
                }
            }

            // 3) concat → 최종 mp4 (재인코딩으로 파라미터 통일)
            Path out = work.resolve("reel.mp4");
            List<String> concatCmd = List.of(
                    ffmpegPath, "-y",
                    "-f", "concat", "-safe", "0",
                    "-i", list.toString(),
                    "-c:v", "libx264", "-preset", "veryfast", "-crf", "23",
                    "-c:a", "aac", "-b:a", "128k",
                    "-movflags", "+faststart",
                    out.toString()
            );
            run(concatCmd, work);

            // 4) 업로드 (R2 등) → mediaAssets 레코드 생성 → shortReel.outputMediaId 연결
            StorageUploader.UploadResult up = storageUploader.upload(out, "video/mp4", "reels/" + reel.getTrip().getId() + "/reel.mp4");
            MediaAsset output = mediaAssetRepository.save(
                    MediaAsset.builder()
                            .contentType(ContentType.VIDEO)
                            .uploader(reel.getCreator())
                            .trip(reel.getTrip())
                            .mediaKind(MediaKind.VIDEO)
                            .captureType(CaptureType.VIDEO)
                            .url(up.url())
                            .thumbnailUrl(null) // 있으면
                            .width(null).height(null)        // 적절히
                            .durationSec(null) // 선택
                            .takenAt(LocalDateTime.now())
                            .isSharedInAlbum(false)
                            .build()
            );
            reel.setOutputMedia(output);
            reel.setRenderStatus(ReelRenderStatus.DONE);
            shortReelRepository.save(reel);
        } catch (Exception e) {
            logger.info("[RENDER_SERVICE] Error: {}", e.getMessage());
            reel.setRenderStatus(ReelRenderStatus.FAILED);
            shortReelRepository.save(reel);
            // 로그/알림


        } finally {
            // Files.walk(work) ... 삭제는 운영 정책에 맞게
        }
    }

    private void run(List<String> cmd, Path wd) throws IOException, InterruptedException {
        Process p = new ProcessBuilder(cmd).directory(wd.toFile()).redirectErrorStream(true).start();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            for (String line; (line = r.readLine()) != null; ) { /* log if needed */ }
        }
        int code = p.waitFor();
        if (code != 0) throw new IllegalStateException("FFmpeg failed: " + String.join(" ", cmd));
    }

    public int calcDurationSec(Path video, String ffprobePath) throws IOException, InterruptedException {
        Process p = new ProcessBuilder(
                ffprobePath, "-v", "error",
                "-select_streams", "v:0",
                "-show_entries", "format=duration",
                "-of", "default=nk=1:nw=1",
                video.toString()
        ).redirectErrorStream(true).start();

        try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line = r.readLine();
            p.waitFor();
            if (line == null) return 0;
            double sec = Double.parseDouble(line);
            return (int) Math.round(sec);
        }
    }

}
