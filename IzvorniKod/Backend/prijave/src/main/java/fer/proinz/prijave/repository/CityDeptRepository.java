package fer.proinz.prijave.repository;

import fer.proinz.prijave.model.CityDept;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityDeptRepository extends JpaRepository<CityDept, Integer> {
}
