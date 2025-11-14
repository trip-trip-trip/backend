package yeohaenggasijo.tripshot.dto.trip.req;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePostReq {
    private String visibility;
    private String caption;

    private List<MediaAttachmentReq> media;
}
