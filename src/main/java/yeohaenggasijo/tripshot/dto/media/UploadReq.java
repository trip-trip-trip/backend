package yeohaenggasijo.tripshot.dto.media;


import jakarta.validation.constraints.*;
public record UploadReq(
        @NotNull Long tripId,
        @NotBlank String fileName,
        @NotBlank String contentType
) {}
