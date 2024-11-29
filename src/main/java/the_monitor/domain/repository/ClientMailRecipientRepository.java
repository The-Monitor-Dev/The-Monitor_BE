package the_monitor.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import the_monitor.domain.model.Client;
import the_monitor.domain.model.ClientMailRecipient;

import java.util.List;

public interface ClientMailRecipientRepository extends JpaRepository<ClientMailRecipient, Long> {

    @Query("SELECT cmr FROM ClientMailRecipient cmr WHERE cmr.client = :client")
    List<ClientMailRecipient> findAllByClient(@Param("client") Client client);

    @Modifying
    @Query("DELETE FROM ClientMailRecipient cmr WHERE cmr.client = :client")
    void deleteAllByClient(@Param("client") Client client);

}
