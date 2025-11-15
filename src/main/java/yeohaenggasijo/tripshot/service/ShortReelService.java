package yeohaenggasijo.tripshot.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yeohaenggasijo.tripshot.domain.common.ReelRenderStatus;
import yeohaenggasijo.tripshot.domain.media.MediaAsset;
import yeohaenggasijo.tripshot.domain.reel.ShortReel;
import yeohaenggasijo.tripshot.dto.media.CreateMediaAssetReq;
import yeohaenggasijo.tripshot.dto.media.MediaAssetRes;
import yeohaenggasijo.tripshot.dto.reel.CreateReelReq;
import yeohaenggasijo.tripshot.dto.reel.ReelRes;
import yeohaenggasijo.tripshot.repository.MediaAssetRepository;
import yeohaenggasijo.tripshot.repository.ShortReelRepository;
import yeohaenggasijo.tripshot.repository.TripRepository;
import yeohaenggasijo.tripshot.repository.UserRepository;
import yeohaenggasijo.tripshot.security.CurrentUserProvider;

@Service
@RequiredArgsConstructor
public class ShortReelService {
    private final MediaAssetRepository mediaAssetRepository;
    private final UserRepository userRepository;
    private final ShortReelRepository shortReelRepository;
    private final TripRepository tripRepository;
    private final CurrentUserProvider currentUserProvider;
    private final MediaAssetService mediaAssetService;

    public ReelRes createReel(CreateReelReq req) {
        Long loggedInUserId = currentUserProvider.getUserId()
                .orElseThrow(EntityNotFoundException::new);
        CreateMediaAssetReq media = req.media();
        MediaAssetRes storedMedia = mediaAssetService.createMediaAsset(media);
        ShortReel data = shortReelRepository.save(ShortReel.builder()
                        .title(req.title())
                .trip(tripRepository.findById(media.tripId()).orElseThrow(()->new EntityNotFoundException("no trip")))
                .creator(userRepository.findById(loggedInUserId).orElseThrow(()->new EntityNotFoundException("no user")))
                .renderStatus(ReelRenderStatus.DONE)
                        .outputMedia(mediaAssetRepository.findById(storedMedia.mediaAssetId()).orElseThrow(()->new EntityNotFoundException("no media asset")))
                .build()
        );
        return new ReelRes(storedMedia, data.getTitle(), data.getRenderStatus());
    }
}
