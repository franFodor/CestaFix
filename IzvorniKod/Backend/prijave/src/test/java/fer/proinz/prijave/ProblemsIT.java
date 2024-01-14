package fer.proinz.prijave;

import com.fasterxml.jackson.databind.ObjectMapper;
import fer.proinz.prijave.model.*;
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
public class ProblemsIT {

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
    void setUpProblem() {
        try (Connection connection = dataSource.getConnection()) {
            String sqlCategory = "INSERT INTO Category (category_id, category_name) " +
                    "VALUES (?, ?)";

            Category category = Category.builder()
                    .categoryId(1)
                    .categoryName("cat_1")
                    .build();

            PreparedStatement preparedStatementCategory = connection.prepareStatement(sqlCategory);
            preparedStatementCategory.setInt(1, category.getCategoryId());
            preparedStatementCategory.setString(2, category.getCategoryName());
            preparedStatementCategory.executeUpdate();

            String sqlCityDept = "INSERT INTO Citydept (city_dept_id, city_dept_name, category_id) " +
                    "VALUES (?, ?, ?)";

            CityDept cityDept = CityDept.builder()
                    .cityDeptId(1)
                    .cityDeptName("dept_1")
                    .category(category)
                    .build();

            PreparedStatement preparedStatementCityDept = connection.prepareStatement(sqlCityDept);
            preparedStatementCityDept.setLong(1, cityDept.getCityDeptId());
            preparedStatementCityDept.setString(2, cityDept.getCityDeptName());
            preparedStatementCityDept.setInt(3, cityDept.getCategory().getCategoryId());
            preparedStatementCityDept.executeUpdate();

            /*String sqlUser = "INSERT INTO Users (user_id, username, email, password, role) " +
                    "VALUES (?, ?, ?, ?, ?)";*/

            UserDetails userDetails = User.builder()
                    .firstname("John")
                    .lastname("Doe")
                    .password(passwordEncoder.encode("qwertz"))
                    .role(Role.USER)
                    .build();

            this.jwtToken = jwtService.generateToken(userDetails);

            String sqlProblem = "INSERT INTO Problems (problem_id, longitude, latitude, status, category_id) " +
                    "VALUES (?, ?, ?, ?, ?)";

            Problem problem = Problem.builder()
                    .problemId(15)
                    .longitude(45.1234)
                    .latitude(27.3857)
                    .status("U obradi")
                    .category(category)
                    .build();


            PreparedStatement preparedStatementProblem = connection.prepareStatement(sqlProblem);
            preparedStatementProblem.setInt(1, problem.getProblemId());
            preparedStatementProblem.setDouble(2, problem.getLongitude());
            preparedStatementProblem.setDouble(3, problem.getLatitude());
            preparedStatementProblem.setString(4, problem.getStatus());
            preparedStatementProblem.setInt(5, problem.getCategory().getCategoryId());
            preparedStatementProblem.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDownProblem() {
        try (Connection connection = dataSource.getConnection()) {
            String sqlProblem = "DELETE FROM Problems WHERE problem_id = ?";
            PreparedStatement preparedStatementProblem = connection.prepareStatement(sqlProblem);
            preparedStatementProblem.setInt(1, 15);
            preparedStatementProblem.executeUpdate();

            String sqlCityDept = "DELETE FROM CityDept WHERE city_dept_id = ?";
            PreparedStatement preparedStatementCityDept = connection.prepareStatement(sqlCityDept);
            preparedStatementCityDept.setInt(1, 1);
            preparedStatementCityDept.executeUpdate();

            String sqlCategory = "DELETE FROM Category WHERE category_id = ?";
            PreparedStatement preparedStatementCategory = connection.prepareStatement(sqlCategory);
            preparedStatementCategory.setInt(1, 1);
            preparedStatementCategory.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getAllProblemsAndExpect200OK() throws Exception {
        mockMvc.perform(get("/public/problem/getAll"))
                .andExpect(status().isOk());
    }

    @Test
    public void getProblemByIdAndExpect200OK() throws Exception {
        mockMvc.perform(get("/public/problem/15"))
                .andExpect(status().isOk());
    }

    @Test
    public void createProblemAndExpect200OK() throws Exception {
        Category testCategory = new Category();
        testCategory.setCategoryId(1);
        testCategory.setCategoryName("cat_2");

        Problem problem = Problem.builder()
                .problemId(15)
                .longitude(45.1234)
                .latitude(27.3857)
                .status("U obradi")
                .category(testCategory)
                .build();

        String jsonProblem = objectMapper.writeValueAsString(problem);

        mockMvc.perform(post("/public/problem")
                        .contentType("application/json")
                        .content(jsonProblem))
                .andExpect(status().isOk());
    }

    @Test
    public void updateProblemAndExpect200OK() throws Exception {
        Category testCategory = new Category();
        testCategory.setCategoryId(2);
        testCategory.setCategoryName("cat_2");

        Problem problem = Problem.builder()
                .problemId(15)
                .longitude(45.1234)
                .latitude(27.3857)
                .status("Popravljeno")
                .category(testCategory)
                .build();

        String jsonProblem = objectMapper.writeValueAsString(problem);

        String roleFromToken = (String) jwtService.extractRole(jwtToken);
        if (roleFromToken.equals("STAFF")) {
            mockMvc.perform(patch("/advanced/user/" + problem.getProblemId())
                            .contentType("application/json")
                            .content(jsonProblem))
                    .andExpect(status().isOk());
        } else {
            mockMvc.perform(patch("/advanced/user/" + problem.getProblemId())
                            .contentType("application/json")
                            .content(jsonProblem))
                    .andExpect(status().isForbidden());
        }

    }

    @Test
    public void deleteProblemAndExpect200OK() throws Exception {
        String roleFromToken = (String) jwtService.extractRole(jwtToken);
        if (roleFromToken.equals("STAFF")) {
            mockMvc.perform(delete("/advanced/problem/15"))
                    .andExpect(status().isOk());
        } else {
            mockMvc.perform(delete("/advanced/problem/15"))
                    .andExpect(status().isForbidden());
        }

    }
}
