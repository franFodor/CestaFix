package fer.proinz.prijave;

import com.fasterxml.jackson.databind.ObjectMapper;
import fer.proinz.prijave.model.CityDepartment;
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
            String sqlCityDept = "INSERT INTO Citydept (citydept_id, citydept_name) " +
                    "VALUES (?, ?)";

            CityDepartment cityDept = CityDepartment.builder()
                    .citydeptId(1)
                    .citydeptName("Ured za obnovu javnih povrsina")
                    .build();

            UserDetails userDetails = User.builder()
                    .firstname("John")
                    .lastname("Doe")
                    .password(passwordEncoder.encode("qwertz"))
                    .role(Role.USER)
                    .build();

            this.jwtToken = jwtService.generateToken(userDetails);

            PreparedStatement preparedStatementCityDept = connection.prepareStatement(sqlCityDept);
            preparedStatementCityDept.setLong(1, cityDept.getCitydeptId());
            preparedStatementCityDept.setString(2, cityDept.getCitydeptName());
            preparedStatementCityDept.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDownCityDept() {
        try (Connection connection = dataSource.getConnection()) {
            String sqlCityDept = "DELETE FROM Citydept WHERE citydept_id = ?";
            PreparedStatement preparedStatementCityDept = connection.prepareStatement(sqlCityDept);
            preparedStatementCityDept.setInt(1, 1);
            preparedStatementCityDept.executeUpdate();
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
        CityDepartment cityDept = CityDepartment.builder()
                .citydeptId(5)
                .citydeptName("Ured za odrzavanje vodovodnih cijevi")
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
        CityDepartment cityDept = CityDepartment.builder()
                .citydeptId(18)
                .citydeptName("Ured za zbrinavanje palih drveca")
                .build();

        String jsonCityDept = objectMapper.writeValueAsString(cityDept);

        String roleFromToken = (String) jwtService.extractRole(jwtToken);
        if (roleFromToken.equals("STAFF")) {
            mockMvc.perform(patch("/advanced/cityDept/" + cityDept.getCitydeptId())
                            .contentType("application/json")
                            .content(jsonCityDept))
                    .andExpect(status().isOk());
        } else {
            mockMvc.perform(patch("/advanced/cityDept/" + cityDept.getCitydeptId())
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
