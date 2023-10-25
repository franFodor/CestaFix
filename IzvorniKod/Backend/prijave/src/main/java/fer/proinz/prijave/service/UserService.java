package fer.proinz.prijave.service;

import fer.proinz.prijave.model.User;
import fer.proinz.prijave.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(int userId) {
        return userRepository.findById(userId);
    }

    public void addNewUser(User user) {
        userRepository.save(user);
    }

    public void deleteUser(int userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if(userOptional.isPresent()) {
            userRepository.deleteById(userId);
        } else {
            throw new RuntimeException("user with id " + userId + " does not exists!");
        }
    }

    public User updateUser(int userId, User user) {
        user.setUserId(userId);
        return userRepository.save(user);
    }

}
