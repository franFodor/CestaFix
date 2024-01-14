package fer.proinz.prijave.service;


import fer.proinz.prijave.dto.AuthenticationResponseDto;
import fer.proinz.prijave.model.User;
import fer.proinz.prijave.repository.CityDeptCategoryRepository;
import fer.proinz.prijave.repository.ProblemRepository;
import fer.proinz.prijave.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final CityDeptCategoryRepository cityDeptCategoryRepository;
    private final ProblemRepository problemRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(int userId) {
        return userRepository.findById(userId);
    }

    public User getPersonalData() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return user;
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public AuthenticationResponseDto updateUser(int userId, User updatedUser) {
        /*Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            User saved = userRepository.save(updatedUser);
            return  saved;
        } else {
            throw new NoSuchElementException("No user with this id");
        }*/
        User user1 = userRepository.findById(userId)
                .map(user -> {
                    if (updatedUser.getFirstname() != null) {
                        user.setFirstname(updatedUser.getFirstname());
                    }
                    if (updatedUser.getLastname() != null) {
                        user.setLastname(updatedUser.getLastname());
                    }
                    if (updatedUser.getEmail() != null) {
                        user.setEmail(updatedUser.getEmail());
                    }
                    if (updatedUser.getPassword() != null) {
                        user.setPassword(updatedUser.getPassword());
                    }
                    return userRepository.save(user);
                })
                .orElseThrow(RuntimeException::new);
        var jwtToken = jwtService.generateToken(user1);
        return AuthenticationResponseDto.builder()
                .token(jwtToken)
                .build();
    }

    public ResponseEntity<String> deleteUser(int userId, Authentication authentication) {
        Optional<User> userOptional = userRepository.findById(userId);
        User authenticatedUser = (User) authentication.getPrincipal();

        if (userOptional.isPresent()) {
            userRepository.deleteById(userId);
            return ResponseEntity.ok("User with id " + userId + " is deleted.");
        } else {
            throw new RuntimeException("user with id " + userId + " does not exists!");
        }
    }

}
