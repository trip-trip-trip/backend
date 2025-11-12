package yeohaenggasijo.tripshot.dto.trip.req;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import yeohaenggasijo.tripshot.domain.common.AttachmentType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MediaAttachmentReq {
    @NotNull(message = "미디어 ID는 필수입니다.")
    @JsonProperty("media_id")
    private Long objectId; // 첨부하려는 미디어 파일의 ID (MediaAsset.id)

    @NotNull(message = "미디어 타입은 필수입니다.")
    @JsonProperty("object_type")
    private AttachmentType objectType; // "media" | "short_reel"

}
