package fer.proinz.prijave.controller;

import fer.proinz.prijave.dto.SignUpDto;
import fer.proinz.prijave.model.User;
import fer.proinz.prijave.repository.UserRepository;
import fer.proinz.prijave.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SignUpController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody @Valid SignUpDto signUpDto) {
        userService.registerUser(signUpDto);
        return ResponseEntity.ok("A new user has been created!");
    }

}
