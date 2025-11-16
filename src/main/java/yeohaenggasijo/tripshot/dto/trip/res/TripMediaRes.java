package yeohaenggasijo.tripshot.dto.trip.res;

import yeohaenggasijo.tripshot.dto.photo.PhotoRes;
import yeohaenggasijo.tripshot.dto.reel.ReelItemRes;
import yeohaenggasijo.tripshot.dto.scrapbook.ScrapbookRes;
import yeohaenggasijo.tripshot.dto.reel.ReelRes;

import java.util.List;

public record TripMediaRes(
        List<PhotoRes> photos,
        List<ScrapbookRes> scrapbooks,
        List<ReelItemRes> reelItems,
        ReelRes reel

) {
}
