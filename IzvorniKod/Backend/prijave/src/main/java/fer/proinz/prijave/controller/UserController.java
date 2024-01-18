package fer.proinz.prijave.controller;

import fer.proinz.prijave.exception.NonExistingUserException;
import fer.proinz.prijave.model.User;
import fer.proinz.prijave.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<User> getUserById(@PathVariable("userId") int userId)
            throws NonExistingUserException {
        return ResponseEntity.ok(userService
                .getUserById(userId)
                .orElseThrow(NonExistingUserException::new)
        );
    }

    @Operation(summary = "Access personal data")
    @GetMapping("/normal/user/whoAmI")
    public ResponseEntity<User> getPersonalData() {
        return ResponseEntity.ok(userService.getPersonalData());
    }

    @Operation(summary = "Create a user")
    @PostMapping("/normal/user")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @Operation(summary = "Update a user",
            description = "Users can update only personal data about themselves")
    @PatchMapping("/advanced/user/{userId}")
    public ResponseEntity<?> updateUser(
            @PathVariable("userId") int userId,
            @RequestBody User updatedUser
        ) throws NonExistingUserException {
        return ResponseEntity.ok(userService.updateUser(userId, updatedUser));
    }

    @Operation(summary = "Delete a user")
    @DeleteMapping("/advanced/user/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable("userId") int userId)
            throws NonExistingUserException {
        return userService.deleteUser(userId);
    }

}
