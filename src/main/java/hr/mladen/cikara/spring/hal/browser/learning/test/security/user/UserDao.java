package hr.mladen.cikara.spring.hal.browser.learning.test.security.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends CrudRepository<User, Long> {
  User findByUsername(String username);
}

