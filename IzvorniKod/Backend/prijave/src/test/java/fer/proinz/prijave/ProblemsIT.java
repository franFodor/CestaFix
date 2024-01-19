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
import java.util.ArrayList;
import java.util.List;

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

            // Problem1
            String sqlProblem1 = "INSERT INTO Problems (problem_id, latitude, longitude, status, category_id) " +
                    "VALUES (?, ?, ?, ?, ?)";
            Problem problem1 = Problem.builder()
                    .problemId(20)
                    .latitude(45.8006)
                    .longitude(15.9713)
                    .status("U obradi")
                    .category(category)
                    .build();
            PreparedStatement preparedStatementProblem1 = connection.prepareStatement(sqlProblem1);
            preparedStatementProblem1.setInt(1, problem1.getProblemId());
            preparedStatementProblem1.setDouble(2, problem1.getLatitude());
            preparedStatementProblem1.setDouble(3, problem1.getLongitude());
            preparedStatementProblem1.setString(4, problem1.getStatus());
            preparedStatementProblem1.setInt(5, problem1.getCategory().getCategoryId());
            preparedStatementProblem1.executeUpdate();

            // Problem2
            String sqlProblem2 = "INSERT INTO Problems (problem_id, latitude, longitude, status, category_id) " +
                    "VALUES (?, ?, ?, ?, ?)";
            Problem problem2 = Problem.builder()
                    .problemId(21)
                    .latitude(45.8009)
                    .longitude(15.9717)
                    .status("U obradi")
                    .category(category)
                    .build();
            PreparedStatement preparedStatementProblem2 = connection.prepareStatement(sqlProblem2);
            preparedStatementProblem2.setInt(1, problem2.getProblemId());
            preparedStatementProblem2.setDouble(2, problem2.getLatitude());
            preparedStatementProblem2.setDouble(3, problem2.getLongitude());
            preparedStatementProblem2.setString(4, problem2.getStatus());
            preparedStatementProblem2.setInt(5, problem2.getCategory().getCategoryId());
            preparedStatementProblem2.executeUpdate();

            // Report1
            String sqlReport1 = "INSERT INTO Reports " +
                    "(report_id, user_id, title, description, address, status, latitude, longitude, problem_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            Report report1 = Report.builder()
                    .reportId(20)
                    .user(user1)
                    .title("Prva prijava")
                    .description("Opis prve prijave")
                    .address("Unska ulica 3")
                    .status("U obradi")
                    .latitude(45.8006)
                    .longitude(15.9713)
                    .problem(problem1)
                    .build();
            PreparedStatement preparedStatementReport1 = connection.prepareStatement(sqlReport1);
            preparedStatementReport1.setInt(1, report1.getReportId());
            preparedStatementReport1.setInt(2, report1.getUser().getUserId());
            preparedStatementReport1.setString(3, report1.getTitle());
            preparedStatementReport1.setString(4, report1.getDescription());
            preparedStatementReport1.setString(5, report1.getAddress());
            preparedStatementReport1.setString(6, report1.getStatus());
            preparedStatementReport1.setDouble(7, report1.getLatitude());
            preparedStatementReport1.setDouble(8, report1.getLongitude());
            preparedStatementReport1.setInt(9, report1.getProblem().getProblemId());
            preparedStatementReport1.executeUpdate();

            // Report2
            String sqlReport2 = "INSERT INTO Reports " +
                    "(report_id, user_id, title, description, address, status, latitude, longitude, problem_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            Report report2 = Report.builder()
                    .reportId(21)
                    .user(user1)
                    .title("Druga prijava")
                    .description("Opis druge prijave")
                    .address("Unska ulica 3")
                    .status("U obradi")
                    .latitude(45.8009)
                    .longitude(15.9717)
                    .problem(problem2)
                    .build();
            PreparedStatement preparedStatementReport2 = connection.prepareStatement(sqlReport2);
            preparedStatementReport2.setInt(1, report2.getReportId());
            preparedStatementReport2.setInt(2, report2.getUser().getUserId());
            preparedStatementReport2.setString(3, report2.getTitle());
            preparedStatementReport2.setString(4, report2.getDescription());
            preparedStatementReport2.setString(5, report2.getAddress());
            preparedStatementReport2.setString(6, report2.getStatus());
            preparedStatementReport2.setDouble(7, report2.getLatitude());
            preparedStatementReport2.setDouble(8, report2.getLongitude());
            preparedStatementReport2.setInt(9, report2.getProblem().getProblemId());
            preparedStatementReport2.executeUpdate();
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
    public void getProblemsForCityDeptAndExpect200OK() throws Exception {
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
    public void groupReportsAndExpect200OK() throws Exception {
        List<Integer> reportIdList = new ArrayList<>();
        reportIdList.add(21);

        String jsonReportIdList = objectMapper.writeValueAsString(reportIdList);

        String roleFromToken = (String) jwtService.extractRole(staffJwtToken);
        if (roleFromToken.equals("ROLE_STAFF")) {
            mockMvc.perform(patch("/advanced/problem/group/20")
                            .contentType("application/json")
                            .header("Authorization", "Bearer " + staffJwtToken)
                            .content(jsonReportIdList))
                    .andExpect(status().isOk());
        } else {
            mockMvc.perform(patch("/advanced/problem/group/20")
                            .contentType("application/json")
                            .header("Authorization", "Bearer " + userJwtToken)
                            .content(jsonReportIdList))
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
