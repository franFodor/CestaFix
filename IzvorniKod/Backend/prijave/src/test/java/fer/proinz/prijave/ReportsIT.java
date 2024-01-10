package fer.proinz.prijave;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fer.proinz.prijave.dto.CreateReportRequestDto;
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
    void setUpCategory() {
        try (Connection connection = dataSource.getConnection()) {
            String sqlCat = "INSERT INTO Category (category_id, category_name) VALUES (1, 'categ1')";
            PreparedStatement preparedStatementReport = connection.prepareStatement(sqlCat);
            preparedStatementReport.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDownCategory() {
        try (Connection connection = dataSource.getConnection()) {
            String sqlCat = "DELETE FROM Category WHERE category_id = 1";
            PreparedStatement preparedStatementReport = connection.prepareStatement(sqlCat);
            preparedStatementReport.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @BeforeEach
    void setUpReport() {
        try (Connection connection = dataSource.getConnection()) {
            String sqlReport = "INSERT INTO Reports (report_id, title, description, address, photo, status, longitude, latitude) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            Report report = Report.builder()
                    .reportId(15)
                    .title("Pukotina na cesti")
                    .description("kwerwoirwsnfsffowefsg")
                    .address("Ulica grada Vukovara 3")
                    .photo(null)
                    .status("Osteceno")
                    .longitude(45.80666)
                    .latitude(15.9696)
                    .build();

            PreparedStatement preparedStatementReport = connection.prepareStatement(sqlReport);
            preparedStatementReport.setInt(1, report.getReportId());
            preparedStatementReport.setString(2, report.getTitle());
            preparedStatementReport.setString(3, report.getDescription());
            preparedStatementReport.setString(4, report.getAddress());
            preparedStatementReport.setBytes(5, report.getPhoto());
            preparedStatementReport.setString(6, report.getStatus());
            preparedStatementReport.setDouble(7, report.getLongitude());
            preparedStatementReport.setDouble(8, report.getLatitude());

            preparedStatementReport.executeUpdate();

            String sqlUser = "INSERT INTO Users (user_id, firstname, lastname, email, password, role) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            User user = User.builder()
                    .userId(2)
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
            preparedStatementUser.setInt(1, user.getUserId());
            preparedStatementUser.setString(2, user.getFirstname());
            preparedStatementUser.setString(3, user.getLastname());
            preparedStatementUser.setString(4, user.getEmail());
            preparedStatementUser.setString(5, user.getPassword());
            preparedStatementUser.setString(6, String.valueOf(user.getRole()));

            preparedStatementUser.executeUpdate();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDownReport() {
        try (Connection connection = dataSource.getConnection()) {
            String sqlReport = "DELETE FROM Reports WHERE report_id = ?";
            PreparedStatement preparedStatementReport = connection.prepareStatement(sqlReport);
            preparedStatementReport.setInt(1, 15);
            preparedStatementReport.executeUpdate();

            String sqlUser = "DELETE FROM Users WHERE user_id = ?";
            PreparedStatement preparedStatementUser = connection.prepareStatement(sqlUser);
            preparedStatementUser.setInt(1, 2);
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
        mockMvc.perform(get("/public/report/15"))
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
                .photo(null)
                .mergeProblemId(null)
                .problemStatus("Osteceno")
                .reportStatus("Osteceno2")
                .longitude(45.80666)
                .latitude(15.9696)
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
                .photo(null)
                .mergeProblemId(null)
                .problemStatus("Osteceno")
                .reportStatus("Osteceno2")
                .longitude(45.80666)
                .latitude(15.9696)
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
                .reportId(15)
                .title("Pukotina na trotoaru")
                .description("nsjgaowjsks")
                .address("Ulica grada Vukovara 5")
                .photo(null)
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
            mockMvc.perform(delete("/advanced/report/15"))
                    .andExpect(status().isOk());
        } else {
            mockMvc.perform(delete("/advanced/report/15"))
                    .andExpect(status().isForbidden());
        }

    }
}
