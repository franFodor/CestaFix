package fer.proinz.prijave.repository;

import fer.proinz.prijave.model.CityDepartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityDepartmentRepository extends JpaRepository<CityDepartment, Integer> {

}
