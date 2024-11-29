package the_monitor.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import the_monitor.domain.model.Account;
import the_monitor.domain.model.Client;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    List<Client> findAllByAccountId(@Param("accountId") Long accountId);

    List<Client> findByAccountAndNameContainingIgnoreCase(Account account, String name);

    Optional<Client> findByIdAndAccountId(@Param("clientId") Long clientId, @Param("accountId") Long accountId);

}