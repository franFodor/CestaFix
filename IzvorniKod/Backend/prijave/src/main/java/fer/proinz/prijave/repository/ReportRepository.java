package fer.proinz.prijave.repository;

import fer.proinz.prijave.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {

    int countByStatus(String status);

}
