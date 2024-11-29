package the_monitor.domain.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import the_monitor.domain.model.Client;
import the_monitor.domain.model.Scrap;
import the_monitor.infrastructure.persistence.JpaScrapRepository;

import java.util.List;
import java.util.Optional;

public interface ScrapRepository extends JpaScrapRepository {

    @Query("SELECT s FROM Scrap s WHERE s.client = :client AND s.title = :title AND s.url = :url")
    Optional<Scrap> findByClientAndTitleAndUrl(@Param("client") Client client,
                                               @Param("title") String title,
                                               @Param("url") String url);

    @Query("SELECT s FROM Scrap s " +
            "WHERE s.client.id = :clientId")
    List<Scrap> findAllByClientId(@Param("clientId") Long clientId);

}
