package fer.proinz.prijave.repository;

import fer.proinz.prijave.model.CityDepartment;
import fer.proinz.prijave.model.CityDeptCategory;
import fer.proinz.prijave.model.CityDeptCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityDeptCategoryRepository extends JpaRepository<CityDeptCategory, Integer> {
    List<CityDeptCategory> findByCityDepartment(CityDepartment cityDepartment);

}
