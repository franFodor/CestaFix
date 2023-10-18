package fer.proinz.prijave.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PrijaveController {

    @GetMapping( "/hello")
    public ResponseEntity<String> PrijavaHello() {
        return ResponseEntity.ok("Prijavili ste prijavu!");
    }
}
