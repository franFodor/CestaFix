package fer.proinz.prijave.repository;

import fer.proinz.prijave.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {

    int countByStatus(String status);

    Optional<Report> findByBusinessId(UUID businessId);

}
