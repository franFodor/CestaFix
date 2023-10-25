package fer.proinz.prijave.controller;

import fer.proinz.prijave.model.User;
import fer.proinz.prijave.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/getAllUsers")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> userList = userService.getAllUsers();
        return ResponseEntity.ok(userList);
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

    @PostMapping()
    public ResponseEntity<String> createNewUser(@RequestBody User user) {
        userService.addNewUser(user);
        return ResponseEntity.ok("Dodan je novi korisnik");
    }

    @PutMapping("/{userId}")
    public ResponseEntity<String> updateUser(
            @PathVariable("userId") int userId, @RequestBody User user) {
        return null;
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable("userId") int userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok("Izbrisali ste korisnika sa id=" + userId);
    }

}
