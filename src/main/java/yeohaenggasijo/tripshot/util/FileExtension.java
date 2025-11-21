package yeohaenggasijo.tripshot.util;

import org.springframework.stereotype.Component;

@Component
public class FileExtension {
    public String guessExt(String original, String contentType, boolean isVideo) {
        String lower = original.toLowerCase();
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "jpg";
        if (lower.endsWith(".png")) return "png";
        if (lower.endsWith(".heic")) return "heic";
        if (lower.endsWith(".mp4")) return "mp4";
        if (lower.endsWith(".mov")) return "mov";

        if (contentType != null) {
            if (contentType.contains("jpeg")) return "jpg";
            if (contentType.contains("png")) return "png";
            if (contentType.contains("heic")) return "heic";
            if (contentType.contains("mp4")) return "mp4";
            if (contentType.contains("quicktime")) return "mov";
        }
        // 기본값
        return isVideo ? "mp4" : "jpg";
    }
}
