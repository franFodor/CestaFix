package fer.proinz.prijave.repository;

import fer.proinz.prijave.model.CityDepartment;
import fer.proinz.prijave.model.CitydepCategory;
import fer.proinz.prijave.model.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityDeptCategoryRepository extends JpaRepository<CitydepCategory, Integer> {
    List<CitydepCategory> findByCityDepartment(CityDepartment cityDepartment);

}
