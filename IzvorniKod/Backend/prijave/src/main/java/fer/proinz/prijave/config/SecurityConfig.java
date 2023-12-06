package fer.proinz.prijave.config;

import io.jsonwebtoken.Jwt;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/auth/**").permitAll()

                        .requestMatchers("/actuator/health").permitAll()

                        .requestMatchers("/public/**").permitAll()
                        .requestMatchers("/advanced/**").hasRole("STAFF")
                        .requestMatchers("/normal/**").hasRole("USER")
                        .anyRequest().authenticated()

                        /*.requestMatchers("/user/getAllUsers").authenticated()
                        .requestMatchers(HttpMethod.GET, "/user/**").hasAnyRole("USER", "STAFF")
                        .requestMatchers(HttpMethod.POST, "/user").hasAnyRole("USER", "STAFF")
                        .requestMatchers(HttpMethod.PUT, "/user/**").hasRole("STAFF")
                        .requestMatchers(HttpMethod.DELETE, "/user/**").hasRole("STAFF")

                        .requestMatchers("/report/getAllReports").permitAll()
                        .requestMatchers(HttpMethod.GET, "/report/**").hasAnyRole("USER", "STAFF")
                        .requestMatchers(HttpMethod.POST, "/report").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/report/**").hasRole("STAFF")
                        .requestMatchers(HttpMethod.DELETE, "/report/**").hasRole("STAFF")*/
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

}
