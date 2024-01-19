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
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String userJwtToken;
    private String staffJwtToken;

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
            //normalUser.setInt(7, user1.getCityDept().getCityDeptId());
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDownCityDept() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "Users");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "Citydept");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "Category");
    }

    @Test
    public void getAllCityDeptsAndExpect200OK() throws Exception {
        mockMvc.perform(get("/public/cityDept/getAll"))
                .andExpect(status().isOk());
    }

    @Test
    public void getCityDeptByIdAndExpect200OK() throws Exception {
        mockMvc.perform(get("/public/cityDept/20"))
                .andExpect(status().isOk());
    }

    @Test
    public void createCityDeptAndExpect200OK() throws Exception {
        Category category = Category.builder()
                .categoryName("Nova kategorija")
                .build();

        CityDept cityDept = CityDept.builder()
                .cityDeptName("Novi gradski ured")
                .category(category)
                .build();

        String jsonCityDept = objectMapper.writeValueAsString(cityDept);

        String roleFromToken = (String) jwtService.extractRole(staffJwtToken);
        if (roleFromToken.equals("ROLE_STAFF")) {
            mockMvc.perform(post("/advanced/cityDept")
                            .contentType("application/json")
                            .header("Authorization", "Bearer " + staffJwtToken)
                            .content(jsonCityDept))
                    .andExpect(status().isOk());
        } else {
            mockMvc.perform(post("/advanced/cityDept")
                            .contentType("application/json")
                            .header("Authorization", "Bearer " + userJwtToken)
                            .content(jsonCityDept))
                    .andExpect(status().isForbidden());
        }
    }

    @Test
    public void updateCityDeptAndExpect200OK() throws Exception {
        CityDept cityDept = CityDept.builder()
                .cityDeptName("Ažurirani gradski ured")
                .build();

        String jsonCityDept = objectMapper.writeValueAsString(cityDept);

        String roleFromToken = (String) jwtService.extractRole(staffJwtToken);
        if (roleFromToken.equals("ROLE_STAFF")) {
            mockMvc.perform(patch("/advanced/cityDept/20")
                            .contentType("application/json")
                            .header("Authorization", "Bearer " + staffJwtToken)
                            .content(jsonCityDept))
                    .andExpect(status().isOk());
        } else {
            mockMvc.perform(patch("/advanced/cityDept/20")
                            .contentType("application/json")
                            .header("Authorization", "Bearer " + userJwtToken)
                            .content(jsonCityDept))
                    .andExpect(status().isForbidden());
        }
    }

     @Test
    public void deleteCityDeptAndExpect200OK() throws Exception {
         String roleFromToken = (String) jwtService.extractRole(staffJwtToken);
         if (roleFromToken.equals("ROLE_STAFF")) {
             mockMvc.perform(delete("/advanced/cityDept/20")
                             .header("Authorization", "Bearer " + staffJwtToken))
                     .andExpect(status().isOk());
         } else {
             mockMvc.perform(delete("/advanced/cityDept/20")
                             .header("Authorization", "Bearer " + userJwtToken))
                     .andExpect(status().isForbidden());
         }
     }

}
