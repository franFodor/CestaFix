package fer.proinz.prijave;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fer.proinz.prijave.dto.CreateReportRequestDto;
import fer.proinz.prijave.model.Category;
import fer.proinz.prijave.model.Report;
import fer.proinz.prijave.model.Role;
import fer.proinz.prijave.model.User;
import fer.proinz.prijave.service.JwtService;
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

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class ReportsIT {

    @Autowired
    private DataSource dataSource;
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    private String jwtToken;

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
    void setUpReport() {
        try (Connection connection = dataSource.getConnection()) {
            String sqlCategory = "INSERT INTO Category (category_name) " +
                    "VALUES (?)";
            Category category = Category.builder()
                    .categoryName("cat_1")
                    .build();
            PreparedStatement preparedStatementCategory = connection.prepareStatement(sqlCategory);
            preparedStatementCategory.setString(1, category.getCategoryName());
            preparedStatementCategory.executeUpdate();

            String sqlReport = "INSERT INTO Reports (title, description, address, status, latitude, longitude) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            Report report = Report.builder()
                    .title("Prva prijava")
                    .description("Opis prijave")
                    .address("Unska ulica 3")
                    .status("U obradi")
                    .latitude(45.80666)
                    .longitude(15.9696)
                    .build();
            PreparedStatement preparedStatementReport = connection.prepareStatement(sqlReport);
            preparedStatementReport.setString(1, report.getTitle());
            preparedStatementReport.setString(2, report.getDescription());
            preparedStatementReport.setString(3, report.getAddress());
            preparedStatementReport.setString(4, report.getStatus());
            preparedStatementReport.setDouble(5, report.getLatitude());
            preparedStatementReport.setDouble(6, report.getLongitude());
            preparedStatementReport.executeUpdate();

            String sqlUser = "INSERT INTO Users (firstname, lastname, email, password, role) " +
                    "VALUES (?, ?, ?, ?, ?)";
            User user = User.builder()
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
            this.jwtToken = jwtService.generateToken(userDetails);
            PreparedStatement preparedStatementUser = connection.prepareStatement(sqlUser);
            preparedStatementUser.setString(1, user.getFirstname());
            preparedStatementUser.setString(2, user.getLastname());
            preparedStatementUser.setString(3, user.getEmail());
            preparedStatementUser.setString(4, user.getPassword());
            preparedStatementUser.setString(5, String.valueOf(user.getRole()));
            preparedStatementUser.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDownReport() {
        try (Connection connection = dataSource.getConnection()) {
            String sqlCategory = "DELETE FROM Category WHERE category_id = ?";
            PreparedStatement preparedStatementCategory = connection.prepareStatement(sqlCategory);
            preparedStatementCategory.setInt(1, 1);
            preparedStatementCategory.executeUpdate();

            String sqlReport = "DELETE FROM Reports WHERE report_id = ?";
            PreparedStatement preparedStatementReport = connection.prepareStatement(sqlReport);
            preparedStatementReport.setInt(1, 1);
            preparedStatementReport.executeUpdate();

            String sqlUser = "DELETE FROM Users WHERE user_id = ?";
            PreparedStatement preparedStatementUser = connection.prepareStatement(sqlUser);
            preparedStatementUser.setInt(1, 1);
            preparedStatementUser.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Test
    public void getAllReportsAndExpect200OK() throws Exception {
        mockMvc.perform(get("/public/report/getAll"))
                .andExpect(status().isOk());
    }

    @Test
    public void getReportByIdAndExpect200OK() throws Exception {
        mockMvc.perform(get("/public/report/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void createReportAuthenticated() throws Exception {
        User testUser = new User();
        testUser.setUserId(2);
        testUser.setRole(Role.USER);

        CreateReportRequestDto report = CreateReportRequestDto.builder()
                .title("Pukotina na cesti")
                .description("kwerwoirwsnfsffowefsg")
                .address("Ulica grada Vukovara 3")
                .mergeProblemId(null)
                .problemStatus("Osteceno")
                .reportStatus("Osteceno2")
                .latitude(15.9696)
                .longitude(45.80666)
                .categoryId(1)
                .build();

        String jsonReport = objectMapper.writeValueAsString(report);

        mockMvc.perform(post("/public/report")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType("application/json")
                        .content(jsonReport))
                .andExpect(status().isOk());
    }

    @Test
    public void createReportAnonymous() throws Exception {

        CreateReportRequestDto report = CreateReportRequestDto.builder()
                .title("Pukotina na cesti")
                .description("kwerwoirwsnfsffowefsg")
                .address("Ulica grada Vukovara 3")
                .mergeProblemId(null)
                .problemStatus("Osteceno")
                .reportStatus("Osteceno2")
                .latitude(15.9696)
                .longitude(45.80666)
                .categoryId(1)
                .build();

        String jsonReport = objectMapper.writeValueAsString(report);

        mockMvc.perform(post("/public/report")
                        .contentType("application/json")
                        .content(jsonReport))
                .andExpect(status().isOk());
    }

    @Test
    public void updateReportAndExpect200OK() throws Exception {
        Report report = Report.builder()
                .title("Pukotina na trotoaru")
                .description("nsjgaowjsks")
                .address("Ulica grada Vukovara 5")
                .status("Osteceno")
                .build();

        String jsonReport = objectMapper.writeValueAsString(report);

        String roleFromToken = (String) jwtService.extractRole(jwtToken);
        if (roleFromToken.equals("STAFF")) {
            mockMvc.perform(patch("/advanced/report/" + report.getReportId())
                            .contentType("application/json")
                            .content(jsonReport))
                    .andExpect(status().isOk());
        } else {
            mockMvc.perform(patch("/advanced/report/" + report.getReportId())
                            .contentType("application/json")
                            .content(jsonReport))
                    .andExpect(status().isForbidden());
        }


    }

    @Test
    public void deleteReportAndExpect200OK() throws Exception {
        String roleFromToken = (String) jwtService.extractRole(jwtToken);
        if (roleFromToken.equals("STAFF")) {
            mockMvc.perform(delete("/advanced/report/1"))
                    .andExpect(status().isOk());
        } else {
            mockMvc.perform(delete("/advanced/report/1"))
                    .andExpect(status().isForbidden());
        }

    }
}
