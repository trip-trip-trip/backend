package yeohaenggasijo.tripshot.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yeohaenggasijo.tripshot.domain.common.InvitationStatus;
import yeohaenggasijo.tripshot.domain.common.TripParticipantRole;
import yeohaenggasijo.tripshot.domain.trip.Trip;
import yeohaenggasijo.tripshot.domain.trip.TripInvitation;
import yeohaenggasijo.tripshot.domain.trip.TripParticipant;
import yeohaenggasijo.tripshot.domain.user.User;
import yeohaenggasijo.tripshot.domain.common.TripStatus;
import yeohaenggasijo.tripshot.dto.trip.req.TripInviteCreateReq;
import yeohaenggasijo.tripshot.dto.trip.req.TripInvitationRespondReq;
import yeohaenggasijo.tripshot.dto.trip.res.InvitationToUserRes;
import yeohaenggasijo.tripshot.dto.trip.res.TripInvitationListRes;
import yeohaenggasijo.tripshot.dto.trip.res.TripInvitationRes;
import yeohaenggasijo.tripshot.exception.BadRequestException;
import yeohaenggasijo.tripshot.repository.TripInvitationRepository;
import yeohaenggasijo.tripshot.repository.TripParticipantRepository;
import yeohaenggasijo.tripshot.repository.TripRepository;
import yeohaenggasijo.tripshot.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TripInvitationService {

    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final TripInvitationRepository tripInvitationRepository;
    private final TripParticipantRepository tripParticipantRepository;

    /* ========= 초대 보내기 ========= */

    @Transactional
    public TripInvitationListRes sendInvitations(Long inviterId, Long tripId, TripInviteCreateReq req) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new EntityNotFoundException("Trip not found: " + tripId));

        if (!trip.getOwner().getId().equals(inviterId)) {
            throw new BadRequestException("이 여행에 대한 초대 권한이 없습니다.");
        }

        if (req.inviteeUserIds() == null || req.inviteeUserIds().isEmpty()) {
            throw new BadRequestException("inviteeUserIds 는 비어 있을 수 없습니다.");
        }

        List<TripInvitationRes> created = new ArrayList<>();

        for (Long inviteeId : req.inviteeUserIds()) {
            if (inviteeId.equals(inviterId)) {
                throw new BadRequestException("자기 자신에게는 초대할 수 없습니다.");
            }

            User invitee = userRepository.findById(inviteeId)
                    .orElseThrow(() -> new EntityNotFoundException("Invitee not found: " + inviteeId));

            // 이미 참가자인지
            if (tripParticipantRepository.existsByTrip_IdAndUser_Id(tripId, inviteeId)) {
                throw new BadRequestException("이미 여행에 참여 중인 유저입니다. id=" + inviteeId);
            }

            // PENDING 초대가 이미 있는지
            boolean pendingExists = tripInvitationRepository
                    .existsByTrip_IdAndInvitee_IdAndStatus(tripId, inviteeId, InvitationStatus.PENDING);

            if (pendingExists) {
                throw new BadRequestException("이미 대기 중인 초대가 있습니다. id=" + inviteeId);
            }

            TripInvitation invitation = TripInvitation.builder()
                    .trip(trip)
                    .inviter(getUserOrThrow(inviterId))
                    .invitee(invitee)
                    .status(InvitationStatus.PENDING)
                    .build();

            TripInvitation saved = tripInvitationRepository.save(invitation);
            created.add(toRes(saved));
        }

        return new TripInvitationListRes(
                trip.getId(),
                trip.getTitle(),
                "SENT",
                created
        );
    }

    /* ========= 초대 현황 조회 ========= */

    @Transactional(readOnly = true)
    public TripInvitationListRes getTripInvitations(Long userId, Long tripId, String directionRaw) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new EntityNotFoundException("Trip not found: " + tripId));

        String direction = (directionRaw == null || directionRaw.isBlank())
                ? "SENT"
                : directionRaw.toUpperCase();

        List<TripInvitation> list;

        switch (direction) {
            case "SENT" -> {
                if (!trip.getOwner().getId().equals(userId)) {
                    throw new BadRequestException("이 여행의 초대 목록을 조회할 권한이 없습니다.");
                }
                list = tripInvitationRepository.findByTrip_IdAndInviter_Id(tripId, userId);
            }
            case "RECEIVED" -> list = tripInvitationRepository.findByTrip_IdAndInvitee_Id(tripId, userId);
            default -> throw new BadRequestException("direction 값은 SENT 또는 RECEIVED 여야 합니다.");
        }

        List<TripInvitationRes> resList = list.stream()
                .map(this::toRes)
                .toList();

        return new TripInvitationListRes(
                trip.getId(),
                trip.getTitle(),
                direction,
                resList
        );
    }

    @Transactional
    public List<InvitationToUserRes> getTripInvitationsToMe(Long uid) {
        List<TripInvitation> tripInvitationList = tripInvitationRepository.findByInvitee_id(uid);
        List<TripInvitation> tiListOnlyPending = tripInvitationList.stream()
                .filter(ti -> ti.getStatus() == InvitationStatus.PENDING)
                .toList();

        return (List<InvitationToUserRes>) tiListOnlyPending.stream()
                .map(this::from)
                .toList();
    }

    @Transactional
    public InvitationToUserRes from(TripInvitation tripInvitation) {
        return new InvitationToUserRes(
                tripInvitation.getId(),
                tripInvitation.getInvitee().getId(),
                tripInvitation.getCreatedAt(),
                tripInvitation.getInviter().getAvatarUrl(),
                tripInvitation.getInviter().getUsername(),
                tripInvitation.getTrip().getTitle(),
                tripInvitation.getTrip().getId(),
                tripInvitation.getStatus()
        );
    }
    /* ========= 초대 수락/거절 ========= */

    /* ========= 초대 수락/거절 ========= */

    /* ========= 초대 수락/거절 ========= */

    @Transactional
    public TripInvitationRes respondInvitation(Long userId, Long invitationId, TripInvitationRespondReq req) {
        TripInvitation invitation = tripInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new EntityNotFoundException("Invitation not found: " + invitationId));

        if (!invitation.getInvitee().getId().equals(userId)) {
            throw new BadRequestException("이 초대에 응답할 권한이 없습니다.");
        }

        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new BadRequestException("이미 처리된 초대입니다.");
        }

        String decision = req.decision().toUpperCase();
        LocalDateTime now = LocalDateTime.now();

        switch (decision) {
            case "ACCEPT" -> {
                // 1) Check if the user is already participating in an ACTIVE trip
//                boolean alreadyTraveling = tripParticipantRepository
//                        .existsByUser_IdAndTrip_Status(userId, TripStatus.ACTIVE);
                boolean  alreadyTraveling = tripParticipantRepository.existsActiveTripForUser(userId, LocalDate.now());

                if (alreadyTraveling) {
                    // Business rule: a user cannot accept a new trip while already traveling
                    throw new BadRequestException("이미 진행 중인 여행이 있어 초대를 수락할 수 없습니다.");
                }

                // 2) Mark invitation as accepted
                invitation.setStatus(InvitationStatus.ACCEPTED);
                invitation.setRespondedAt(now);

                // 3) Add user as a participant if not already joined
                Long tripId = invitation.getTrip().getId();
                if (!tripParticipantRepository.existsByTrip_IdAndUser_Id(tripId, userId)) {
                    TripParticipant participant = TripParticipant.builder()
                            .trip(invitation.getTrip())
                            .user(invitation.getInvitee())
                            .role(TripParticipantRole.EDITOR)
                            .build();
                    tripParticipantRepository.save(participant);
                }
            }
            case "REJECT" -> {
                invitation.setStatus(InvitationStatus.DECLINED);
                invitation.setRespondedAt(now);
            }
            default -> throw new BadRequestException("decision 값은 ACCEPT 또는 REJECT 여야 합니다.");
        }

        return toRes(invitation);
    }
    /* ========= 초대 삭제(취소) ========= */

    @Transactional
    public void cancelInvitation(Long userId, Long invitationId) {
        TripInvitation invitation = tripInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new EntityNotFoundException("Invitation not found: " + invitationId));

        if (!invitation.getInviter().getId().equals(userId)) {
            throw new BadRequestException("이 초대를 삭제할 권한이 없습니다.");
        }

        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new BadRequestException("이미 처리된 초대는 삭제할 수 없습니다.");
        }

        tripInvitationRepository.delete(invitation);
    }



    /* ========= 내부 유틸 ========= */

    private User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
    }

    private TripInvitationRes toRes(TripInvitation inv) {
        Trip trip = inv.getTrip();
        User inviter = inv.getInviter();
        User invitee = inv.getInvitee();

        return new TripInvitationRes(
                inv.getId(),
                trip.getId(),
                trip.getTitle(),
                inviter.getId(),
                inviter.getUsername(),
                inviter.getTag(),
                inviter.getAvatarUrl(),
                invitee.getId(),
                invitee.getUsername(),
                invitee.getTag(),
                invitee.getAvatarUrl(),
                inv.getStatus().name(),
                inv.getCreatedAt(),
                inv.getRespondedAt()
        );
    }
}
