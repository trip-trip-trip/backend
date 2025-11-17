package yeohaenggasijo.tripshot.dto.login.res;

public record TryLoginRes(
        Boolean success,
        TokenRes token
) {
}
