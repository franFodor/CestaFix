package fer.proinz.prijave;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fer.proinz.prijave.model.CityDepartment;
import fer.proinz.prijave.model.Role;
import fer.proinz.prijave.model.User;
import fer.proinz.prijave.service.JwtService;
import fer.proinz.prijave.service.UserDetail;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class UsersIT {

    @Autowired
    private DataSource dataSource;
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    private String jwtTokenForUser;

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer  = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("postgres")
            .withUsername("postgres")
            .withPassword("postgres");

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) throws SQLException {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("default.enabled", () -> false);
    }

    @BeforeEach
    void setUpUser() {
        try (Connection connection = dataSource.getConnection()) {
            String sqlUser = "INSERT INTO Users (user_id, username, email, password, role) " +
                    "VALUES (?, ?, ?, ?, ?)";

            User user = User.builder()
                    .userId(4)
                    .firstname("John")
                    .lastname("Doe")
                    .email("john.doe@gmail.com")
                    .password(passwordEncoder.encode("qwertz"))
                    .role(Role.USER)
                    .build();

            UserDetails userDetails = User.builder()
                    .firstname("John")
                    .lastname("Doe")
                    .password(passwordEncoder.encode("qwertz"))
                    .role(Role.USER)
                    .build();

            this.jwtTokenForUser = jwtService.generateToken(userDetails);

            PreparedStatement preparedStatementUser = connection.prepareStatement(sqlUser);
            preparedStatementUser.setLong(1, user.getUserId());
            preparedStatementUser.setString(2, user.getFirstname());
            preparedStatementUser.setString(3, user.getLastname());
            preparedStatementUser.setString(4, user.getEmail());
            preparedStatementUser.setString(5, user.getPassword());
            preparedStatementUser.setString(6, String.valueOf(user.getRole()));

            preparedStatementUser.executeUpdate();

            String sqlCityDepartment = "INSERT INTO Citydep (citydep_id, citydep_name) " +
                    "VALUES (?, ?)";

            CityDepartment cityDepartment = CityDepartment.builder()
                    .citydeptId(1)
                    .citydeptName("Ured za obnovu javnih povrsina")
                    .build();

            PreparedStatement preparedStatementCityDepartment = connection.prepareStatement(sqlCityDepartment);
            preparedStatementCityDepartment.setLong(1, cityDepartment.getCitydeptId());
            preparedStatementCityDepartment.setString(2, cityDepartment.getCitydeptName());

            preparedStatementCityDepartment.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDownReport() {
        try (Connection connection = dataSource.getConnection()) {
            String sqlUser = "DELETE FROM Users WHERE user_id = ?";
            PreparedStatement preparedStatementUser = connection.prepareStatement(sqlUser);
            preparedStatementUser.setInt(1, 4);
            preparedStatementUser.executeUpdate();

            String sqlCityDepartment = "DELETE FROM Citydep WHERE citydep_id = ?";
            PreparedStatement preparedStatementCityDepartment = connection.prepareStatement(sqlCityDepartment);
            preparedStatementCityDepartment.setInt(1, 1);
            preparedStatementCityDepartment.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getAllUsersAndExpect200OK() throws Exception {
        mockMvc
                .perform(
                        get("/advanced/user/getAll")
                                .header("Authorization", "Bearer " + jwtTokenForUser))
                .andExpect(status().isOk());
    }

    @Test
    public void getUserByIdAndExpect200OK() throws Exception {
        mockMvc
                .perform(get("/normal/user/4")
                        .header("Authorization", "Bearer " + jwtTokenForUser))
                .andExpect(status().isOk());
    }

    @Test
    public void createUserAndExpect200OK() throws Exception {
        CityDepartment testCitydep = new CityDepartment();
        testCitydep.setCitydeptId(1);

        User user = User.builder()
                .userId(4)
                .firstname("Mat")
                .lastname("Waller")
                .email("mat.waller@gmail.com")
                .password(passwordEncoder.encode("qwertz"))
                .role(Role.USER)
                .citydept(null)
                .build();

        String jsonReport = objectMapper.writeValueAsString(user);

        mockMvc
                .perform(post("/normal/user")
                        .header("Authorization", "Bearer " + jwtTokenForUser)
                        .contentType("application/json")
                        .content(jsonReport))
                .andExpect(status().isOk());
    }

    @Test
    public void updateUserAndExpect200OK() throws Exception {
        CityDepartment testCitydep = new CityDepartment();
        testCitydep.setCitydeptId(1);

        User user = User.builder()
                .userId(4)
                .firstname("Mat")
                .lastname("Waller")
                .email("mat.waller@gmail.com")
                .password(passwordEncoder.encode("qwertz"))
                .role(Role.USER)
                .citydept(null)
                .build();

        String jsonReport = objectMapper.writeValueAsString(user);

        mockMvc
                .perform(
                        put("/advanced/user/" + user.getUserId())
                                .header("Authorization", "Bearer " + jwtTokenForUser)
                                .contentType("application/json")
                                .content(jsonReport))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteUserAndExpect200OK() throws Exception {
        mockMvc
                .perform(
                        delete("/advanced/user/4")
                                .header("Authorization", "Bearer " + jwtTokenForUser))
                .andExpect(status().isOk());
    }

}
