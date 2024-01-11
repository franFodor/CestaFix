package fer.proinz.prijave.model;

import java.io.Serializable;
import java.util.Objects;

public class CityDeptCategoryId implements Serializable {
    private int category; // This type should match the type of the primary key of Category
    private int cityDepartment; // This type should match the type of the primary key of CityDepartment

    // Default constructor
    public CityDeptCategoryId() {
    }

    // All-args constructor
    public CityDeptCategoryId(int category, int cityDepartment) {
        this.category = category;
        this.cityDepartment = cityDepartment;
    }

    // Getters, setters, hashCode, and equals methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CityDeptCategoryId that = (CityDeptCategoryId) o;
        return category == that.category && cityDepartment == that.cityDepartment;
    }

    @Override
    public int hashCode() {
        return Objects.hash(category, cityDepartment);
    }
}
