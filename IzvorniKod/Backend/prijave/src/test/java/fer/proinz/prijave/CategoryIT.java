package fer.proinz.prijave;

import com.fasterxml.jackson.databind.ObjectMapper;
import fer.proinz.prijave.model.Category;
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
public class CategoryIT {

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
            String sqlCategory = "INSERT INTO Category (category_id, category_name) " +
                    "VALUES (?, ?)";

            Category category = Category.builder()
                    .categoryId(1)
                    .categoryName("Vodovodno oštećenje")
                    .build();

            PreparedStatement preparedStatementCategory = connection.prepareStatement(sqlCategory);
            preparedStatementCategory.setInt(1, category.getCategoryId());
            preparedStatementCategory.setString(2, category.getCategoryName());
            preparedStatementCategory.executeUpdate();

            UserDetails userDetails = User.builder()
                    .firstname("John")
                    .lastname("Doe")
                    .password(passwordEncoder.encode("qwertz"))
                    .role(Role.USER)
                    .build();

            this.jwtToken = jwtService.generateToken(userDetails);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDownCategory() {
        try (Connection connection = dataSource.getConnection()) {
            String sqlCategory = "DELETE FROM Category WHERE category_id = ?";
            PreparedStatement preparedStatementCategory = connection.prepareStatement(sqlCategory);
            preparedStatementCategory.setInt(1, 1);
            preparedStatementCategory.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getAllCategoriesAndExpect200OK() throws Exception {
        mockMvc.perform(get("/public/category/getAll"))
                .andExpect(status().isOk());
    }

    @Test
    public void getCategoryByIdAndExpect200OK() throws Exception {
        mockMvc.perform(get("/public/category/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void createCategoryAndExpect200OK() throws Exception {
        Category category = Category.builder()
                .categoryId(5)
                .categoryName("Oštećenje kolnika")
                .build();

        String jsonCategory = objectMapper.writeValueAsString(category);

        String roleFromToken = (String) jwtService.extractRole(jwtToken);
        if (roleFromToken.equals("STAFF")) {
            mockMvc.perform(post("/advanced/category")
                            .contentType("application/json")
                            .content(jsonCategory))
                    .andExpect(status().isOk());
        } else {
            mockMvc.perform(post("/advanced/category")
                            .contentType("application/json")
                            .content(jsonCategory))
                    .andExpect(status().isForbidden());
        }
    }

    @Test
    public void updateCategoryAndExpect200OK() throws Exception {
        Category category = Category.builder()
                .categoryId(1)
                .categoryName("Električno oštećenje")
                .build();

        String jsonCategory = objectMapper.writeValueAsString(category);

        String roleFromToken = (String) jwtService.extractRole(jwtToken);
        if (roleFromToken.equals("STAFF")) {
            mockMvc.perform(put("/advanced/category/" + category.getCategoryId())
                            .contentType("application/json")
                            .content(jsonCategory))
                    .andExpect(status().isOk());
        } else {
            mockMvc.perform(put("/advanced/category/" + category.getCategoryId())
                            .contentType("application/json")
                            .content(jsonCategory))
                    .andExpect(status().isForbidden());
        }
    }

    @Test
    public void deleteCategoryAndExpect200OK() throws Exception {
        String roleFromToken = (String) jwtService.extractRole(jwtToken);
        if (roleFromToken.equals("STAFF")) {
            mockMvc.perform(delete("/advanced/category/1"))
                    .andExpect(status().isOk());
        } else {
            mockMvc.perform(delete("/advanced/category/1"))
                    .andExpect(status().isForbidden());
        }
    }
}
