package yeohaenggasijo.tripshot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import yeohaenggasijo.tripshot.dto.ApiResponse;
import yeohaenggasijo.tripshot.security.CurrentUserProvider;

@RestController
class PingController {

    private final CurrentUserProvider current;
    PingController(CurrentUserProvider current) {this.current = current;}

    @GetMapping("/whoami")
    ApiResponse<Long> whoami() { return ApiResponse.ok(current.requireUserId()); }

    @GetMapping("/ping")
    public String ping() { return "pong"; }
}