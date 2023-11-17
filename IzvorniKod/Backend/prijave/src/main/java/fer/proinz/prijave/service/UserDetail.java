package fer.proinz.prijave.service;

import fer.proinz.prijave.model.User;
import fer.proinz.prijave.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetail implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isPresent()) {
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.get().getUsername())
                    .password(user.get().getPassword())
                    .roles(String.valueOf(user.get().getRole()))
                    .build();
        } else {
            throw new UsernameNotFoundException("User with this email does not exists!");
        }
    }
}
