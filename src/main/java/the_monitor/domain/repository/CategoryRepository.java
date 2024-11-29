package the_monitor.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import the_monitor.domain.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
