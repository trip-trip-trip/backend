package yeohaenggasijo.tripshot.dto.reel;

public record ReelStatusRes(
        String status,          // QUEUED | RENDERING | DONE | FAILED | NOT_APPLICABLE(여행 미종료)
        Long reelId,            // 있을 때만
        String outputUrl        // DONE일 때만
) {
}
