package fer.proinz.prijave;

import com.fasterxml.jackson.databind.ObjectMapper;
import fer.proinz.prijave.model.Report;
import org.flywaydb.core.internal.jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.github.dockerjava.core.MediaType;

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
public class ReportsIT {

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
    void setUpReport() {
        try (Connection connection = dataSource.getConnection()) {
            String sql = "INSERT INTO Reports (report_id, title, description, location_coordinates, address, report_time, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

            Report report = new Report.builder()
                    .reportId(15L)
                    .title("Pukotina na cesti")
                    .description("kwerwoirwsnfsffowefsg")
                    .locationCoordinates("194702742235")
                    .address("Ulica grada Vukovara 3")
                    .reportTime(Timestamp.from(Instant.now()))
                    .status("Osteceno")
                    .build();

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, report.getReportId());
            preparedStatement.setString(2, report.getTitle());
            preparedStatement.setString(3, report.getDescription());
            preparedStatement.setString(4, report.getLocationCoordinates());
            preparedStatement.setString(5, report.getAddress());
            preparedStatement.setTimestamp(6, report.getReportTime());
            preparedStatement.setString(7, report.getStatus());

            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDownReport() {
        try (Connection connection = dataSource.getConnection()) {
            String sql = "DELETE FROM Reports WHERE report_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, 15L);
            preparedStatement.executeUpdate();

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
    public void createNewReportAndExpect200OK() throws Exception {
        Report report = new Report.builder()
                .reportId(15L)
                .title("Pukotina na cesti")
                .description("kwerwoirwsnfsffowefsg")
                .locationCoordinates("194702742235")
                .address("Ulica grada Vukovara 3")
                .reportTime(Timestamp.from(Instant.now()))
                .status("Osteceno")
                .build();

        String jsonReport = objectMapper.writeValueAsString(report);

        mockMvc
                .perform(post("/report")
                                .contentType("application/json")
                                .content(jsonReport))
                .andExpect(status().isOk());
    }

    @Test
    public void updateReportAndExpect200OK() throws Exception {

        Report report = new Report.builder()
                .reportId(15L)
                .title("Pukotina na cesti")
                .description("kwerwoirwsnfsffowefsg")
                .locationCoordinates("194702742235")
                .address("Ulica grada Vukovara 3")
                .reportTime(Timestamp.from(Instant.now()))
                .status("Osteceno")
                .build();


        String jsonReport = objectMapper.writeValueAsString(report);

        mockMvc
                .perform(
                        put("/report/" + report.getReportId())
                                .contentType("application/json")
                                .content(jsonReport))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteReportAndExpect200OK() throws Exception {
        mockMvc
                .perform(
                        delete("/report/15"))
                .andExpect(status().isOk());
    }
}
