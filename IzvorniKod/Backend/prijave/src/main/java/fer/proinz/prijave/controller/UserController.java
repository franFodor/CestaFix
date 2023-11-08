package fer.proinz.prijave.controller;

import fer.proinz.prijave.model.User;
import fer.proinz.prijave.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/getAllUsers")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/get/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable("userId") int userId) {
        Optional<User> userOptional = userService.getUserById(userId);
        if(userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Secured("ADMIN")
    @PostMapping("")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User saved = userService.createUser(user);
        return ResponseEntity
                .created(URI.create("/user/" + saved.getUserId()))
                .body(saved);
    }

    @Secured("ADMIN")
    @PutMapping("/{userId}")
    public User updateUser(
            @PathVariable("userId") int userId,
            @RequestBody User updatedUser
        ) {
        return userService.updateUser(userId, updatedUser);
    }

    @Secured("ADMIN")
    @DeleteMapping("/{userId}")
    public Optional<User> deleteUser(@PathVariable("userId") int userId) {
        return userService.deleteUser(userId);
    }

}
