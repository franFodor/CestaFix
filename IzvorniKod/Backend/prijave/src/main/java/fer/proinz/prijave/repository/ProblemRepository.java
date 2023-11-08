package fer.proinz.prijave.repository;

import fer.proinz.prijave.model.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface ProblemRepository extends JpaRepository<Problem, Integer> {

}
