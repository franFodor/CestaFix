package fer.proinz.prijave.service;


import fer.proinz.prijave.exception.NonExistingUserException;
import fer.proinz.prijave.model.Role;
import fer.proinz.prijave.model.User;
import fer.proinz.prijave.repository.ReportRepository;
import fer.proinz.prijave.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final ReportRepository reportRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(int userId) {
        return userRepository.findById(userId);
    }

    public User getPersonalData() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(int userId, User updatedUser) throws NonExistingUserException {
        return userRepository.findById(userId)
                .map(user -> {
                    if (updatedUser.getFirstname() != null) {
                        user.setFirstname(updatedUser.getFirstname());
                    }
                    if (updatedUser.getLastname() != null) {
                        user.setLastname(updatedUser.getLastname());
                    }
                    return userRepository.save(user);
                })
                .orElseThrow(NonExistingUserException::new);
    }

    public ResponseEntity<String> deleteUser(int userId) throws NonExistingUserException {
        User user = userRepository
                .findById(userId)
                .orElseThrow(NonExistingUserException::new);
        User authenticatedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (authenticatedUser.getRole() == Role.STAFF || authenticatedUser.getUserId() == userId) {
            // Find all of user's reports and set the user attribute in them to null (anonymous)
            reportRepository.findByUser(user)
                    .forEach(report -> report.setUser(null));

            userRepository.deleteById(userId);
            return ResponseEntity.ok("User with id " + userId + " is deleted.");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You cannot delete other users.");
        }
    }

}
