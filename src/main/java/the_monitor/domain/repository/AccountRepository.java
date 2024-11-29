package the_monitor.domain.repository;

import org.springframework.stereotype.Repository;
import the_monitor.domain.model.Account;
import the_monitor.infrastructure.persistence.JpaAccountRepository;

@Repository
public interface AccountRepository extends JpaAccountRepository {

    Account findAccountByEmail(String email);

    Account findAccountById(Long id);

}
