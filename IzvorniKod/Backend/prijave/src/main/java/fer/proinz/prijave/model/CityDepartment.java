package fer.proinz.prijave.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Citydept")
public class CityDepartment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int citydeptId;

    private String citydeptName;
}
