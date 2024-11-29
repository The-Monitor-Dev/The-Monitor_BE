package the_monitor.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import the_monitor.domain.model.Account;

@Repository
public interface JpaAccountRepository extends JpaRepository<Account, Long> {
}
