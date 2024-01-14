package fer.proinz.prijave;

import com.fasterxml.jackson.databind.ObjectMapper;
import fer.proinz.prijave.model.Category;
import fer.proinz.prijave.model.CityDept;
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
public class CityDeptIT {

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
    void setUpCityDept() {
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

            UserDetails userDetails = User.builder()
                    .firstname("John")
                    .lastname("Doe")
                    .password(passwordEncoder.encode("qwertz"))
                    .role(Role.USER)
                    .build();

            this.jwtToken = jwtService.generateToken(userDetails);

            PreparedStatement preparedStatementCityDept = connection.prepareStatement(sqlCityDept);
            preparedStatementCityDept.setLong(1, cityDept.getCityDeptId());
            preparedStatementCityDept.setString(2, cityDept.getCityDeptName());
            preparedStatementCityDept.setInt(3, cityDept.getCategory().getCategoryId());
            preparedStatementCityDept.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDownCityDept() {
        try (Connection connection = dataSource.getConnection()) {
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
    public void getAllCityDeptsAndExpect200OK() throws Exception {
        mockMvc.perform(get("/public/cityDept/getAll"))
                .andExpect(status().isOk());
    }

    @Test
    public void getCityDeptByIdAndExpect200OK() throws Exception {
        mockMvc.perform(get("/public/cityDept/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void createCityDeptAndExpect200OK() throws Exception {
        CityDept cityDept = CityDept.builder()
                .cityDeptId(5)
                .cityDeptName("Ured za odrzavanje vodovodnih cijevi")
                .build();

        String jsonCityDept = objectMapper.writeValueAsString(cityDept);

        String roleFromToken = (String) jwtService.extractRole(jwtToken);
        if (roleFromToken.equals("STAFF")) {
            mockMvc.perform(post("/advanced/cityDept")
                            .contentType("application/json")
                            .content(jsonCityDept))
                    .andExpect(status().isOk());
        } else {
            mockMvc.perform(post("/advanced/cityDept")
                            .contentType("application/json")
                            .content(jsonCityDept))
                    .andExpect(status().isForbidden());
        }
    }

    @Test
    public void updateCityDeptAndExpect200OK() throws Exception {
        CityDept cityDept = CityDept.builder()
                .cityDeptId(18)
                .cityDeptName("Ured za zbrinavanje palih drveca")
                .build();

        String jsonCityDept = objectMapper.writeValueAsString(cityDept);

        String roleFromToken = (String) jwtService.extractRole(jwtToken);
        if (roleFromToken.equals("STAFF")) {
            mockMvc.perform(patch("/advanced/cityDept/" + cityDept.getCityDeptId())
                            .contentType("application/json")
                            .content(jsonCityDept))
                    .andExpect(status().isOk());
        } else {
            mockMvc.perform(patch("/advanced/cityDept/" + cityDept.getCityDeptId())
                            .contentType("application/json")
                            .content(jsonCityDept))
                    .andExpect(status().isForbidden());
        }
    }

     @Test
    public void deleteCityDeptAndExpect200OK() throws Exception {
         String roleFromToken = (String) jwtService.extractRole(jwtToken);
         if (roleFromToken.equals("STAFF")) {
             mockMvc.perform(delete("/advanced/cityDept/1"))
                     .andExpect(status().isOk());
         } else {
             mockMvc.perform(delete("/advanced/cityDept/1"))
                     .andExpect(status().isForbidden());
         }
     }

}
