package fer.proinz.prijave;

import com.fasterxml.jackson.databind.ObjectMapper;
import fer.proinz.prijave.model.*;
import fer.proinz.prijave.repository.CategoryRepository;
import fer.proinz.prijave.service.JwtService;
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
public class ProblemsIT {

    @Autowired
    private DataSource dataSource;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String userJwtToken;
    private String staffJwtToken;

    @Autowired
    private CategoryRepository categoryRepository;

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
    void setUpProblem() {
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
    void tearDownProblem() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "Reports");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "Problems");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "Users");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "Citydept");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "Category");
    }

    @Test
    public void getAllProblemsAndExpect200OK() throws Exception {
        mockMvc.perform(get("/public/problem/getAll"))
                .andExpect(status().isOk());
    }

    @Test
    public void getProblemByIdAndExpect200OK() throws Exception {
        mockMvc.perform(get("/public/problem/20"))
                .andExpect(status().isOk());
    }

    @Test
    public void createProblemAndExpect200OK() throws Exception {
        Category category = Category.builder()
                .categoryName("cat_create")
                .build();
        categoryRepository.save(category);

        Problem problem = Problem.builder()
                .latitude(45.8197)
                .longitude(16.0178)
                .status("U obradi")
                .category(category)
                .build();

        String jsonProblem = objectMapper.writeValueAsString(problem);

        mockMvc.perform(post("/public/problem")
                        .contentType("application/json")
                        .content(jsonProblem))
                .andExpect(status().isOk());
    }

    @Test
    public void updateProblemAndExpect200OK() throws Exception {
        Problem problem = Problem.builder()
                .status("Obrađeno")
                .build();

        String jsonProblem = objectMapper.writeValueAsString(problem);

        String roleFromToken = (String) jwtService.extractRole(staffJwtToken);
        if (roleFromToken.equals("ROLE_STAFF")) {
            mockMvc.perform(patch("/advanced/problem/20")
                            .contentType("application/json")
                            .header("Authorization", "Bearer " + staffJwtToken)
                            .content(jsonProblem))
                    .andExpect(status().isOk());
        } else {
            mockMvc.perform(patch("/advanced/problem/20")
                            .contentType("application/json")
                            .header("Authorization", "Bearer " + userJwtToken)
                            .content(jsonProblem))
                    .andExpect(status().isForbidden());
        }

    }

    @Test
    public void deleteProblemAndExpect200OK() throws Exception {
        String roleFromToken = (String) jwtService.extractRole(staffJwtToken);
        if (roleFromToken.equals("ROLE_STAFF")) {
            mockMvc.perform(delete("/advanced/problem/20")
                            .header("Authorization", "Bearer " + staffJwtToken))
                    .andExpect(status().isOk());
        } else {
            mockMvc.perform(delete("/advanced/problem/20")
                            .header("Authorization", "Bearer " + userJwtToken))
                    .andExpect(status().isForbidden());
        }

    }
}
