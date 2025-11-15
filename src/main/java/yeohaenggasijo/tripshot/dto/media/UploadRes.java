package yeohaenggasijo.tripshot.dto.media;
import java.util.List;
import java.util.Map;

public record UploadRes(
        String key,
        String uploadUrl,
        Map<String, List<String>> uploadHeaders,
        String publicUrl,      // 선택: 퍼블릭 도메인 사용하는 경우
        String downloadUrl     // 선택: presigned GET (예: 10분)
) {}
