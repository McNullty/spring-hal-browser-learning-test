package hr.mladen.cikara.spring.hal.browser.learning.test.security.user;

import java.util.Collection;

public interface UserService {

  User save(User user);
  Collection<User> findAll();
  void delete(long id);
}
