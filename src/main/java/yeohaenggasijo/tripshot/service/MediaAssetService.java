package yeohaenggasijo.tripshot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yeohaenggasijo.tripshot.dto.media.CreateMediaAssetReq;
import yeohaenggasijo.tripshot.dto.media.MediaAssetRes;
import yeohaenggasijo.tripshot.repository.MediaAssetRepository;
import yeohaenggasijo.tripshot.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class MediaAssetService {
    private final MediaAssetRepository mediaAssetRepository;
    private final UserRepository userRepository;

    public MediaAssetRes createMediaAsset(CreateMediaAssetReq req) {


    }

}
