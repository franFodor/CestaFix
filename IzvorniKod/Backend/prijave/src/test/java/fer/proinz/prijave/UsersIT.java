package fer.proinz.prijave;

import com.fasterxml.jackson.databind.ObjectMapper;
import fer.proinz.prijave.model.Report;
import fer.proinz.prijave.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
import java.sql.Timestamp;
import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class UsersIT {

    @Autowired
    private DataSource dataSource;
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

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
            String sql = "INSERT INTO Users (user_id, name, email, password, is_admin) " +
                    "VALUES (?, ?, ?, ?, ?)";

            User user = User.builder()
                    .userId(2)
                    .name("John Doe")
                    .email("john.doe@gmail.com")
                    .password("wjs82jas72nw")
                    .isAdmin(false)
                    .build();

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, user.getUserId());
            preparedStatement.setString(2, user.getName());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(4, user.getPassword());
            preparedStatement.setBoolean(5, user.isAdmin());

            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDownReport() {
        try (Connection connection = dataSource.getConnection()) {
            String sql = "DELETE FROM Users WHERE user_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, 2);
            preparedStatement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getAllUsersAndExpect200OK() throws Exception {
        mockMvc
                .perform(
                        get("/user/getAllUsers"))
                .andExpect(status().isOk());
    }

    @Test
    public void getUserByIdAndExpect200OK() throws Exception {
        mockMvc
                .perform(get("/user/get/2"))
                .andExpect(status().isOk());
    }

    @Test
    public void createNewUserAndExpect200OK() throws Exception {
        User user = User.builder()
                .userId(2)
                .name("John Doe")
                .email("john.doe@gmail.com")
                .password("wjs82jas72nw")
                .isAdmin(false)
                .build();

        String jsonReport = objectMapper.writeValueAsString(user);

        mockMvc
                .perform(post("/user")
                        .contentType("application/json")
                        .content(jsonReport))
                .andExpect(status().isOk());
    }

    @Test
    public void updateUserAndExpect200OK() throws Exception {

        User user = User.builder()
                .userId(2)
                .name("John Doe")
                .email("john.doe@gmail.com")
                .password("wjs82jas72nw")
                .isAdmin(false)
                .build();


        String jsonReport = objectMapper.writeValueAsString(user);

        mockMvc
                .perform(
                        put("/user/" + user.getUserId())
                                .contentType("application/json")
                                .content(jsonReport))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteUserAndExpect200OK() throws Exception {
        mockMvc
                .perform(
                        delete("/user/2"))
                .andExpect(status().isOk());
    }

}
