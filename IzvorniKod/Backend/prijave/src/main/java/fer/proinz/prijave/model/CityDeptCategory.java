package fer.proinz.prijave.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "citydeptcategory")
@IdClass(CityDeptCategoryId.class) // Use the composite key class
public class CityDeptCategory {

    @Id
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Id
    @ManyToOne
    @JoinColumn(name = "citydept_id")
    private CityDepartment cityDepartment;
}