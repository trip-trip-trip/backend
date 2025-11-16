package yeohaenggasijo.tripshot.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import yeohaenggasijo.tripshot.domain.common.ContentType;
import yeohaenggasijo.tripshot.domain.common.ScrapbookRenderStatus;
import yeohaenggasijo.tripshot.domain.common.ScrapbookVisibility;
import yeohaenggasijo.tripshot.domain.scrapbook.Scrapbook;
import yeohaenggasijo.tripshot.domain.trip.Trip;
import yeohaenggasijo.tripshot.dto.media.MediaAssetRes;
import yeohaenggasijo.tripshot.dto.reel.CreateReelItemReq;
import yeohaenggasijo.tripshot.dto.scrapbook.CreateScrapbookReq;
import yeohaenggasijo.tripshot.dto.scrapbook.ScrapbookRes;
import yeohaenggasijo.tripshot.repository.MediaAssetRepository;
import yeohaenggasijo.tripshot.repository.ScrapbookRepository;
import yeohaenggasijo.tripshot.repository.TripRepository;
import yeohaenggasijo.tripshot.repository.UserRepository;
import yeohaenggasijo.tripshot.security.CurrentUserProvider;

@Service
@RequiredArgsConstructor
public class ScrapbookService {
    private final TripRepository tripRepository;
    private final MediaAssetRepository mediaAssetRepository;
    private final MediaAssetService mediaAssetService;
    private final CurrentUserProvider currentUser; // 로그인 유저 확인
    private final ScrapbookRepository scrapbookRepository;
    private final UserRepository userRepository;

    public ScrapbookRes createScrapbook(MultipartFile file, CreateScrapbookReq req){
        Long userId = currentUser.requireUserId();
        Trip trip = tripRepository.findById(req.tripId()).orElseThrow(()->new EntityNotFoundException("Trip not found"));
        MediaAssetRes storedMedia = mediaAssetService.createMediaAssetFromMultipart(file, req.media(), ContentType.SCRAPBOOK);
        Scrapbook data = scrapbookRepository.save(
                Scrapbook.builder()
                        .trip(trip)
                        .creator(userRepository.findById(userId).orElseThrow(()->new EntityNotFoundException("User not found")))
                        .title(req.title())
                        .coverMedia(mediaAssetRepository.findById(storedMedia.mediaAssetId()).orElseThrow(()->new EntityNotFoundException("Media not found")))
                        .visibility(ScrapbookVisibility.FRIENDS)
                        .renderStatus(ScrapbookRenderStatus.DONE)
                        .build()
        );
        return new ScrapbookRes(storedMedia, data.getTitle(), data.getVisibility(), data.getRenderStatus());
    }
}
