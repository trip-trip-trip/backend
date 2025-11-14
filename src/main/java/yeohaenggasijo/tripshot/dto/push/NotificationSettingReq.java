package yeohaenggasijo.tripshot.dto.push;

import java.util.List;

public record NotificationSettingReq(
        Long userId,
        Integer timesPerDay,
        List<Integer> timezone
) {
}
