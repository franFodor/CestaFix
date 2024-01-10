package fer.proinz.prijave.service;


import fer.proinz.prijave.model.CitydepCategory;
import fer.proinz.prijave.model.Problem;
import fer.proinz.prijave.model.Role;
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

    public User updateUser(int userId, User updatedUser) {
        /*Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            User saved = userRepository.save(updatedUser);
            return  saved;
        } else {
            throw new NoSuchElementException("No user with this id");
        }*/
        return userRepository.findById(userId)
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

    public List<Problem> getProblemsForUser(User user) {
        // Fetch CitydepCategory instances related to the user's CityDepartment
        List<CitydepCategory> citydepCategories = cityDeptCategoryRepository.findByCityDepartment(user.getCitydept());

        // Extract Category IDs from CitydepCategory instances
        List<Integer> categoryIds = citydepCategories.stream()
                .map(citydepCategory -> citydepCategory.getCategory().getCategoryId())
                .collect(Collectors.toList());

        // Fetch problems for each category ID and flatten the result

        List<Problem> userProblems = categoryIds.stream()
                .flatMap(categoryId -> problemRepository.findByCategory_CategoryId(categoryId).stream())
                .distinct()
                .collect(Collectors.toList());

        return userProblems;
    }

}
