package fer.proinz.prijave.controller;

import fer.proinz.prijave.dto.AuthenticationResponseDto;
import fer.proinz.prijave.service.AuthenticationService;
import fer.proinz.prijave.dto.LoginRequestDto;
import fer.proinz.prijave.dto.RegisterRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponseDto> register(@RequestBody RegisterRequestDto request) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDto> login(@RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(authenticationService.login(request));
    }

}
