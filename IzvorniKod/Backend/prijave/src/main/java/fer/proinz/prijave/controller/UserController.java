package fer.proinz.prijave.controller;

import fer.proinz.prijave.model.User;
import fer.proinz.prijave.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get all users")
    @GetMapping("/advanced/user/getAll")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @Operation(summary = "Get a user by its id")
    @GetMapping("/normal/user/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable("userId") int userId) {
        Optional<User> userOptional = userService.getUserById(userId);
        return userOptional.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Access personal data")
    @GetMapping("/normal/user/whoAmI")
    public ResponseEntity<User> getPersonalData() {
        return ResponseEntity.ok(userService.getPersonalData());
    }

    @Operation(summary = "Create a user")
    @PostMapping("/normal/user")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User saved = userService.createUser(user);
        return ResponseEntity.ok(saved);
    }

    @Operation(summary = "Update a user",
            description = "Users can update only personal data about themselves")
    @PatchMapping("/advanced/user/{userId}")
    public ResponseEntity<User> updateUser(
            @PathVariable("userId") int userId,
            @RequestBody User updatedUser
        ) {
        return ResponseEntity.ok(userService.updateUser(userId, updatedUser));
    }

    //@PreAuthorize("hasRole('STAFF') or (hasRole('USER') and #userId == principal.id)")
    @Operation(summary = "Delete a user")
    @DeleteMapping("/advanced/user/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable("userId") int userId, Authentication authentication) {
        return userService.deleteUser(userId, authentication);
    }

}
