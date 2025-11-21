package yeohaenggasijo.tripshot.service;


import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import yeohaenggasijo.tripshot.domain.common.CaptureType;
import yeohaenggasijo.tripshot.domain.common.ContentType;
import yeohaenggasijo.tripshot.domain.common.MediaKind;
import yeohaenggasijo.tripshot.domain.media.MediaAsset;
import yeohaenggasijo.tripshot.domain.trip.Trip;
import yeohaenggasijo.tripshot.domain.user.User;
import yeohaenggasijo.tripshot.dto.media.CreateMediaAssetReq;
import yeohaenggasijo.tripshot.dto.media.MediaAssetRes;
import yeohaenggasijo.tripshot.repository.MediaAssetRepository;
import yeohaenggasijo.tripshot.repository.TripRepository;
import yeohaenggasijo.tripshot.repository.UserRepository;
import yeohaenggasijo.tripshot.security.CurrentUserProvider;
import yeohaenggasijo.tripshot.service.storage.StorageUploader;
import yeohaenggasijo.tripshot.util.FileExtension;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MediaAssetService {

    private final Logger log = LoggerFactory.getLogger(MediaAssetService.class);
    private final FileExtension fileExtension;
    private final MediaAssetRepository mediaAssetRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final StorageUploader storageUploader;
    private final CurrentUserProvider currentUser; // 로그인 유저 확인
    private static final Logger logger = LoggerFactory.getLogger(MediaAssetService.class);

    @Transactional
    public MediaAssetRes createMediaAssetFromMultipart(MultipartFile file,
                                                       CreateMediaAssetReq meta,
                                                       ContentType type) {

        Long userId = currentUser.requireUserId();
        User uploader = userRepository.findById(userId)
                .orElseThrow(EntityNotFoundException::new);

        Trip trip = tripRepository.findById(meta.tripId())
                .orElseThrow(EntityNotFoundException::new);

        // mediaKind 결정
        String ctype = Optional.ofNullable(file.getContentType()).orElse("");
        boolean isVideo = type == ContentType.VIDEO || ctype.startsWith("video/");
        MediaKind mediaKind = isVideo ? MediaKind.VIDEO : MediaKind.PHOTO;

        // captureType 보정: 영상이면 VIDEO, 아니면 meta.captureType (film|normal)
        CaptureType captureType = isVideo ? CaptureType.VIDEO : CaptureType.valueOf(meta.captureType());

        // objectKey 결정 (trips/{tripId}/{photo|video}/{uuid}.{ext})
        String original = Optional.ofNullable(file.getOriginalFilename()).orElse("");
        String ext = fileExtension.guessExt(original, ctype, isVideo);
        String key = "trips/%d/%s/%s.%s".formatted(
                trip.getId(),
                mediaKind.name().toLowerCase(),
                UUID.randomUUID(),
                ext
        );

        // 임시 파일 생성 후 업로더 호출
        Path tmp = null;
        try {
            tmp = Files.createTempFile("upload-", "." + ext);
            file.transferTo(tmp.toFile());

            StorageUploader.UploadResult up = storageUploader.upload(tmp, ctype, key);
            String url = up.url();
//          String thumb = up.thumbnailUrl();
            String thumb = null;
            // 이미지라면 width/height 추출 시도
            Integer width = null, height = null, durationSec = null;
            if (!isVideo) {
                try {
                    BufferedImage img = ImageIO.read(tmp.toFile());
                    if (img != null) {
                        width = img.getWidth();
                        height = img.getHeight();
                    }
                } catch (Exception ignore) {
                }
            } else {
                // durationSec 추출은 FFprobe 연동 없으면 null로 두거나
                durationSec = null;
            }

            MediaAsset saved = mediaAssetRepository.save(MediaAsset.builder()
                    .contentType(type)
                    .uploader(uploader)
                    .trip(trip)
                    .mediaKind(mediaKind)
                    .captureType(captureType)
                    .comment(meta.comment())
                    .url(url)
                    .thumbnailUrl(thumb)
                    .width(width)
                    .height(height)
                    .durationSec(durationSec)
                    .takenAt(LocalDateTime.now())             // 프론트/EXIF에서 받은 값
                    .isSharedInAlbum(false)    // 선택
                    .expiration(LocalDateTime.now().plusYears(1))       // 선택
                    .build());

            return new MediaAssetRes(
                    saved.getId(),
                    saved.getTrip().getId(),
                    saved.getMediaKind().name(),
                    saved.getContentType().name(),
                    saved.getComment(),
                    saved.getUrl(),
                    saved.getUploader().getId(),
                    saved.getUploader().getUsername(),
                    saved.getWidth(),
                    saved.getHeight(),
                    saved.getDurationSec(),
                    saved.getTakenAt(),
                    saved.getIsSharedInAlbum(),
                    saved.getExpiration()
            );

        } catch (IOException e) {
            logger.info("Could not create media asset of type: {}, error: {}", type, e, e);
            throw new IllegalStateException("파일 업로드 실패", e);
        } finally {
            if (tmp != null) try {
                Files.deleteIfExists(tmp);
            } catch (IOException ignore) {
            }
        }
    }

}
