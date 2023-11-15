package fer.proinz.prijave;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
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
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

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
            String sqlReport = "INSERT INTO Reports (report_id, title, description, address, photo, report_time, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

            Report report = Report.builder()
                    .reportId(15L)
                    .title("Pukotina na cesti")
                    .description("kwerwoirwsnfsffowefsg")
                    .address("Ulica grada Vukovara 3")
                    .photo(null)
                    .reportTime(Timestamp.from(Instant.now()))
                    .status("Osteceno")
                    .build();

            PreparedStatement preparedStatementReport = connection.prepareStatement(sqlReport);
            preparedStatementReport.setLong(1, report.getReportId());
            preparedStatementReport.setString(2, report.getTitle());
            preparedStatementReport.setString(3, report.getDescription());
            preparedStatementReport.setString(4, report.getAddress());
            preparedStatementReport.setBytes(5, report.getPhoto());
            preparedStatementReport.setTimestamp(6, report.getReportTime());
            preparedStatementReport.setString(7, report.getStatus());

            preparedStatementReport.executeUpdate();

            String sqlUser = "INSERT INTO Users (user_id, username, email, password, role) " +
                    "VALUES (?, ?, ?, ?, ?)";

            User user = User.builder()
                    .userId(2)
                    .username("John Doe")
                    .email("john.doe@gmail.com")
                    .password("wjs82jas72nw")
                    .role("USER")
                    .build();

            PreparedStatement preparedStatementUser = connection.prepareStatement(sqlUser);
            preparedStatementUser.setLong(1, user.getUserId());
            preparedStatementUser.setString(2, user.getUsername());
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
            String sqlReport = "DELETE FROM Reports WHERE report_id = ?";
            PreparedStatement preparedStatementReport = connection.prepareStatement(sqlReport);
            preparedStatementReport.setLong(1, 15L);
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
        mockMvc
                .perform(
                        get("/report/getAllReports"))
                .andExpect(status().isOk());
    }

    @Test
    public void getReportByIdAndExpect200OK() throws Exception {
        mockMvc
                .perform(get("/report/get/15"))
                .andExpect(status().isOk());
    }

    @Test
    public void createReportAndExpect201OK() throws Exception {
        User testUser = new User();
        testUser.setUserId(2);

        Report report = Report.builder()
                .reportId(15L)
                .user(testUser)
                .title("Pukotina na cesti")
                .description("kwerwoirwsnfsffowefsg")
                .address("Ulica grada Vukovara 3")
                .photo(null)
                .reportTime(Timestamp.from(Instant.now()))
                .status("Osteceno")
                .build();

        String jsonReport = objectMapper.writeValueAsString(report);

        mockMvc
                .perform(post("/report")
                                .contentType("application/json")
                                .content(jsonReport))
                .andExpect(status().isCreated());
    }

    @Test
    public void updateReportAndExpect200OK() throws Exception {

        Report report = Report.builder()
                .reportId(15L)
                .title("Pukotina na cesti")
                .description("kwerwoirwsnfsffowefsg")
                .address("Ulica grada Vukovara 3")
                .photo(null)
                .reportTime(Timestamp.from(Instant.now()))
                .status("Osteceno")
                .build();


        String jsonReport = objectMapper.writeValueAsString(report);

        mockMvc
                .perform(
                        put("/report/" + report.getReportId())
                                .with(user("admin").roles("ADMIN"))
                                .contentType("application/json")
                                .content(jsonReport))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteReportAndExpect200OK() throws Exception {
        mockMvc
                .perform(
                        delete("/report/15")
                                .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }
}
