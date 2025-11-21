package yeohaenggasijo.tripshot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yeohaenggasijo.tripshot.dto.ApiResponse;
import yeohaenggasijo.tripshot.dto.trip.req.TripInvitationRespondReq;
import yeohaenggasijo.tripshot.dto.trip.res.TripInvitationRes;
import yeohaenggasijo.tripshot.security.CurrentUserProvider;
import yeohaenggasijo.tripshot.service.TripInvitationService;

@RestController
@RequestMapping("/invitations")
@RequiredArgsConstructor
public class TripInvitationController {

    private final TripInvitationService tripInvitationService;
    private final CurrentUserProvider currentUserProvider;

    // 초대 수락/거절
    @PatchMapping("/{invitationId}")
    public ResponseEntity<ApiResponse<TripInvitationRes>> respondInvitation(
            @PathVariable Long invitationId,
            @RequestBody TripInvitationRespondReq req
    ) {
        Long uid = currentUserProvider.requireUserId();
        TripInvitationRes res = tripInvitationService.respondInvitation(uid, invitationId, req);
        return ResponseEntity.ok(ApiResponse.ok(res));
    }

    // 초대 삭제(초대한 쪽)
    @DeleteMapping("/{invitationId}")
    public ResponseEntity<ApiResponse<Void>> deleteInvitation(
            @PathVariable Long invitationId
    ) {
        Long uid = currentUserProvider.requireUserId();
        tripInvitationService.cancelInvitation(uid, invitationId);
        return ResponseEntity.ok(
                ApiResponse.of(true, 200, "초대가 삭제되었습니다.", null)
        );
    }
}
