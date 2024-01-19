package fer.proinz.prijave;

import com.fasterxml.jackson.databind.ObjectMapper;
import fer.proinz.prijave.dto.ReportRequestDto;
import fer.proinz.prijave.exception.NonExistingReportException;
import fer.proinz.prijave.model.*;
import fer.proinz.prijave.repository.ReportRepository;
import fer.proinz.prijave.service.JwtService;
import org.junit.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.jdbc.JdbcTestUtils;
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
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String userJwtToken;
    private String staffJwtToken;

    @Autowired
    private ReportRepository reportRepository;

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
            // Category
            String sqlCategory = "INSERT INTO Category (category_id, category_name) " +
                    "VALUES (?, ?)";
            Category category = Category.builder()
                    .categoryId(20)
                    .categoryName("cat_20")
                    .build();
            PreparedStatement preparedStatementCategory = connection.prepareStatement(sqlCategory);
            preparedStatementCategory.setInt(1, category.getCategoryId());
            preparedStatementCategory.setString(2, category.getCategoryName());
            preparedStatementCategory.executeUpdate();

            // City Dept
            String sqlCityDept = "INSERT INTO Citydept (city_dept_id, city_dept_name, category_id) " +
                    "VALUES (?, ?, ?)";
            CityDept cityDept = CityDept.builder()
                    .cityDeptId(20)
                    .cityDeptName("dept_20")
                    .category(category)
                    .build();
            PreparedStatement preparedStatementCityDept = connection.prepareStatement(sqlCityDept);
            preparedStatementCityDept.setInt(1, cityDept.getCityDeptId());
            preparedStatementCityDept.setString(2, cityDept.getCityDeptName());
            preparedStatementCityDept.setInt(3, cityDept.getCategory().getCategoryId());
            preparedStatementCityDept.executeUpdate();

            // USER user
            String sqlNormalUser = "INSERT INTO Users (user_id, firstname, lastname, email, password, role) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            User user1 = User.builder()
                    .userId(20)
                    .firstname("Obicni")
                    .lastname("Korisnik")
                    .email("obicni.korisnik@gmail.com")
                    .password(passwordEncoder.encode("ObicnaSifra.5"))
                    .role(Role.USER)
                    .cityDept(null)
                    .build();
            PreparedStatement normalUser = connection.prepareStatement(sqlNormalUser);
            normalUser.setInt(1, user1.getUserId());
            normalUser.setString(2, user1.getFirstname());
            normalUser.setString(3, user1.getLastname());
            normalUser.setString(4, user1.getEmail());
            normalUser.setString(5, user1.getPassword());
            normalUser.setString(6, String.valueOf(user1.getRole()));
            normalUser.executeUpdate();
            this.userJwtToken = jwtService.generateToken(user1);

            // STAFF user
            String sqlAdvancedUser = "INSERT INTO Users (user_id, firstname, lastname, email, password, role, city_dept_id)" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            User user2 = User.builder()
                    .userId(21)
                    .firstname("Gradski")
                    .lastname("Službenik")
                    .email("gradski.sluzbenik@gmail.com")
                    .password(passwordEncoder.encode("LaganaSifra.5"))
                    .role(Role.STAFF)
                    .cityDept(cityDept)
                    .build();
            PreparedStatement advancedUser = connection.prepareStatement(sqlAdvancedUser);
            advancedUser.setInt(1, user2.getUserId());
            advancedUser.setString(2, user2.getFirstname());
            advancedUser.setString(3, user2.getLastname());
            advancedUser.setString(4, user2.getEmail());
            advancedUser.setString(5, user2.getPassword());
            advancedUser.setString(6, String.valueOf(user2.getRole()));
            advancedUser.setInt(7, user2.getCityDept().getCityDeptId());
            advancedUser.executeUpdate();
            this.staffJwtToken = jwtService.generateToken(user2);

            // Problem
            String sqlProblem = "INSERT INTO Problems (problem_id, latitude, longitude, status, category_id) " +
                    "VALUES (?, ?, ?, ?, ?)";
            Problem problem = Problem.builder()
                    .problemId(20)
                    .latitude(45.8006)
                    .longitude(15.9713)
                    .status("U obradi")
                    .category(category)
                    .build();
            PreparedStatement preparedStatementProblem = connection.prepareStatement(sqlProblem);
            preparedStatementProblem.setInt(1, problem.getProblemId());
            preparedStatementProblem.setDouble(2, problem.getLatitude());
            preparedStatementProblem.setDouble(3, problem.getLongitude());
            preparedStatementProblem.setString(4, problem.getStatus());
            preparedStatementProblem.setInt(5, problem.getCategory().getCategoryId());
            preparedStatementProblem.executeUpdate();

            // Report
            String sqlReport = "INSERT INTO Reports " +
                    "(report_id, user_id, title, description, address, status, latitude, longitude, problem_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            Report report = Report.builder()
                    .reportId(20)
                    .user(user1)
                    .title("Prva prijava")
                    .description("Opis prijave")
                    .address("Unska ulica 3")
                    .status("U obradi")
                    .latitude(45.8006)
                    .longitude(15.9713)
                    .problem(problem)
                    .build();
            PreparedStatement preparedStatementReport = connection.prepareStatement(sqlReport);
            preparedStatementReport.setInt(1, report.getReportId());
            preparedStatementReport.setInt(2, report.getUser().getUserId());
            preparedStatementReport.setString(3, report.getTitle());
            preparedStatementReport.setString(4, report.getDescription());
            preparedStatementReport.setString(5, report.getAddress());
            preparedStatementReport.setString(6, report.getStatus());
            preparedStatementReport.setDouble(7, report.getLatitude());
            preparedStatementReport.setDouble(8, report.getLongitude());
            preparedStatementReport.setInt(9, report.getProblem().getProblemId());
            preparedStatementReport.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDownReport() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "Reports");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "Problems");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "Users");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "Citydept");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "Category");
    }

    @Test
    public void getAllReportsAndExpect200OK() throws Exception {
        mockMvc.perform(get("/public/report/getAll"))
                .andExpect(status().isOk());
    }

    @Test
    public void getReportByIdAndExpect200OK() throws Exception {
        mockMvc.perform(get("/public/report/20"))
                .andExpect(status().isOk());
    }

    @Test
    public void getReportByBusinessIdAndExpect200OK() throws Exception {
        Report report = reportRepository.findById(20).orElseThrow(NonExistingReportException::new);

        mockMvc.perform(get("/public/lookup/" + report.getBusinessId()))
                .andExpect(status().isOk());
    }

    @Test
    public void getReportStatisticsAndExpect200OK() throws Exception {
        mockMvc.perform(get("/public/statistics"))
                .andExpect(status().isOk());
    }

    @Test
    public void createReportAuthenticated() throws Exception {
        ReportRequestDto report = ReportRequestDto.builder()
                .title("Autenticirana prijava")
                .description("Autenticiran opis")
                .address("Savska Cesta 18")
                .reportStatus("U obradi")
                .latitude(45.8049)
                .longitude(15.9638)
                .problemStatus("U obradi")
                .categoryId(20)
                .mergeProblemId(null)
                .build();

        String jsonReport = objectMapper.writeValueAsString(report);

        mockMvc.perform(post("/public/report")
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + userJwtToken)
                        .content(jsonReport))
                .andExpect(status().isOk());
    }

    @Test
    public void createReportAnonymous() throws Exception {
        ReportRequestDto report = ReportRequestDto.builder()
                .title("Anonimna prijava")
                .description("Anoniman opis")
                .address("Maksimirska cesta 128")
                .reportStatus("U obradi")
                .latitude(45.8197)
                .longitude(16.0178)
                .problemStatus("U obradi")
                .categoryId(20)
                .mergeProblemId(null)
                .build();

        String jsonReport = objectMapper.writeValueAsString(report);

        mockMvc.perform(post("/public/report")
                        .contentType("application/json")
                        .content(jsonReport))
                .andExpect(status().isOk());
    }

    @Test
    public void getNearbyReportAndExpect200OK() throws Exception {
        ReportRequestDto report = ReportRequestDto.builder()
                .title("Provjera za blisku prijavu")
                .description("Opis provjere")
                .address("Maksimirska cesta 128")
                .reportStatus("U obradi")
                .latitude(45.8197)
                .longitude(16.0178)
                .problemStatus("U obradi")
                .categoryId(20)
                .mergeProblemId(null)
                .build();

        String jsonReport = objectMapper.writeValueAsString(report);

        mockMvc.perform(post("/public/nearbyReport")
                        .contentType("application/json")
                        .content(jsonReport))
                .andExpect(status().isOk());
    }

    @Test
    public void updateReportAndExpect200OK() throws Exception {
        Report report = Report.builder()
                .title("Ažurirana prijava")
                .description("Ažuriran opis prijave")
                .status("Obrađeno")
                .build();

        String jsonReport = objectMapper.writeValueAsString(report);

        String roleFromToken = (String) jwtService.extractRole(staffJwtToken);
        if (roleFromToken.equals("ROLE_STAFF")) {
            mockMvc.perform(patch("/advanced/report/20")
                            .contentType("application/json")
                            .header("Authorization", "Bearer " + staffJwtToken)
                            .content(jsonReport))
                    .andExpect(status().isOk());
        } else {
            mockMvc.perform(patch("/advanced/report/20")
                            .contentType("application/json")
                            .header("Authorization", "Bearer " + userJwtToken)
                            .content(jsonReport))
                    .andExpect(status().isForbidden());
        }
    }

    @Test
    public void deleteReportAndExpect200OK() throws Exception {
        String roleFromToken = (String) jwtService.extractRole(staffJwtToken);
        if (roleFromToken.equals("ROLE_STAFF")) {
            mockMvc.perform(delete("/advanced/report/20")
                            .header("Authorization", "Bearer " + staffJwtToken))
                    .andExpect(status().isOk());
        } else {
            mockMvc.perform(delete("/advanced/report/20")
                            .header("Authorization", "Bearer " + userJwtToken))
                    .andExpect(status().isForbidden());
        }
    }

}
