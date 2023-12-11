package fer.proinz.prijave.service;


import fer.proinz.prijave.model.Role;
import fer.proinz.prijave.model.User;
import fer.proinz.prijave.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(int userId) {
        return userRepository.findById(userId);
    }

    public User createUser(User user) {

        return userRepository.save(user);
    }

    public User updateUser(int userId, User updatedUser) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            User saved = userRepository.save(updatedUser);
            return  saved;
        } else {
            throw new NoSuchElementException("No user with this id");
        }


    }

    public ResponseEntity<String> deleteUser(int userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if(userOptional.isPresent()) {
            userRepository.deleteById(userId);
            return ResponseEntity.ok("User with id " + userId + " is deleted.");
        } else {
            throw new RuntimeException("user with id " + userId + " does not exists!");
        }
    }

}
