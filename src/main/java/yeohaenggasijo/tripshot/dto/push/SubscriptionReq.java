package yeohaenggasijo.tripshot.dto.push;

public record SubscriptionReq(
        String userId,
        String endpoint,
        String p256dh, // keys.p256dh
        String auth    // keys.auth
) {}
