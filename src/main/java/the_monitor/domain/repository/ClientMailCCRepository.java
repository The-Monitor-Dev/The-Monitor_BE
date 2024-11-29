package the_monitor.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import the_monitor.domain.model.Client;
import the_monitor.domain.model.ClientMailCC;

import java.util.List;

public interface ClientMailCCRepository extends JpaRepository<ClientMailCC, Long> {

    @Query("SELECT cmc FROM ClientMailCC cmc WHERE cmc.client = :client")
    List<ClientMailCC> findAllByClient(@Param("client") Client client);

    @Modifying
    @Query("DELETE FROM ClientMailCC cmc WHERE cmc.client = :client")
    void deleteAllByClient(@Param("client") Client client);

}
