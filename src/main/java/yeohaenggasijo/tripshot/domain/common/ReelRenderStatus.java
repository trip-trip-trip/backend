package yeohaenggasijo.tripshot.domain.common;

public enum ReelRenderStatus {
    COLLECTING,   // ← 새로 추가: 클립 수집만 하는 상태
    QUEUED,
    RENDERING,
    DONE,
    FAILED
}
