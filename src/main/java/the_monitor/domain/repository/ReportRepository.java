package the_monitor.domain.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.Query;
import the_monitor.domain.model.Report;
import the_monitor.infrastructure.persistence.JpaReportRepository;

import java.util.List;

public interface ReportRepository extends JpaReportRepository {

    @Query("SELECT r FROM Report r WHERE r.client.id = :clientId AND r.id = :reportId")
    Report findReportByClientIdAndReportId(@Param("clientId") Long clientId,
                                           @Param("reportId") Long reportId);

    @Query("SELECT r FROM Report r WHERE r.client.id = :clientId AND r.title LIKE %:searchTitle% ORDER BY r.createdAt DESC")
    List<Report> findByClientIdAndTitleContaining(@Param("clientId") Long clientId,
                                                  @Param("searchTitle") String searchTitle);

}
