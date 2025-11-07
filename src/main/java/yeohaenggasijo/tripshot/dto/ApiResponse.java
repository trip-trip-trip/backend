package yeohaenggasijo.tripshot.dto;

public record ApiResponse<T>(
    boolean success,
    int code,
    String message,
    T data
) {

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, 200, "OK", data);
    }
    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(true, 201, "Created", data);
    }
    public static ApiResponse<Void> noContent() {
        return new ApiResponse<>(true, 204, "No Content", null);
    }
    public static <T> ApiResponse<T> of(boolean success, int code, String message, T data) {
        return new ApiResponse<>(success, code, message, data);
    }
    public static ApiResponse<Void> error(int code, String message) {
        return new ApiResponse<>(false, code, message, null);
    }
}
