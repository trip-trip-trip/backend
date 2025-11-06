package yeohaenggasijo.tripshot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yeohaenggasijo.tripshot.domain.common.InvitationStatus;
import yeohaenggasijo.tripshot.domain.common.TripStatus;
import yeohaenggasijo.tripshot.domain.common.TripVisibility;
import yeohaenggasijo.tripshot.domain.trip.Trip;
import yeohaenggasijo.tripshot.domain.trip.TripInvitation;
import yeohaenggasijo.tripshot.domain.user.User;
import yeohaenggasijo.tripshot.dto.trip.req.TripCreateReq;
import yeohaenggasijo.tripshot.exception.BadRequestException;
import yeohaenggasijo.tripshot.repository.TripInvitationRepository;
import yeohaenggasijo.tripshot.repository.TripRepository;
import yeohaenggasijo.tripshot.repository.UserRepository;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TripService {
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final TripInvitationRepository tripInvitationRepository;
    private final Clock clock;

    @Transactional
    public Trip create(Long uid, TripCreateReq req) {
        User owner = userRepository.findById(uid)
                .orElseThrow(()-> new IllegalArgumentException("User not found"));
        LocalDate today = LocalDate.now(clock);
        if (req.endDate().isBefore(today)) {
            throw new BadRequestException("End date already passed");
        }
        TripStatus status = req.startDate().isAfter(today)
                ? TripStatus.UPCOMING
                : TripStatus.ACTIVE;
        boolean hasInvitees = req.inviteeUserIds() != null && !req.inviteeUserIds().isEmpty();
        TripVisibility visibility = hasInvitees ? TripVisibility.FRIENDS : TripVisibility.PRIVATE;
        Trip trip = Trip.builder()
                .owner(owner)
                .title(req.title())
                .description(req.description())
                .startDate(req.startDate())
                .endDate(req.endDate())
                .visibility(visibility)
                .status(status)
                .build();
        tripRepository.save(trip);
        if (hasInvitees) {
            for (Long inviteeId: req.inviteeUserIds()) {
                if (inviteeId.equals(uid)) continue;
                User invitee = userRepository.findById(inviteeId)
                        .orElseThrow(()-> new IllegalArgumentException("User not found"));
                TripInvitation inv = TripInvitation.builder()
                        .trip(trip)
                        .inviter(owner)
                        .invitee(invitee)
                        .status(InvitationStatus.PENDING)
                        .build();
                tripInvitationRepository.save(inv);
            }
        }
        return trip;
    }
    @Transactional(readOnly = true)
    public List<Trip> myTrips(Long ownerId) {
        return tripRepository.findByOwner_Id(ownerId);
    }
}
