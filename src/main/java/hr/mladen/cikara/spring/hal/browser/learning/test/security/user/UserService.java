package hr.mladen.cikara.spring.hal.browser.learning.test.security.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

  /**
   * Saves user.
   *
   * @param user User entity
   * @return created user with id
   */
  User save(User user);

  /**
   * Finds all Users in repository
   *
   * @param pageable Pagable object
   * @return Page with users
   */
  Page<User> findAll(Pageable pageable);

  /**
   * Deletes user
   * @param id id of user to delete
   */
  void delete(long id);
}
