package hr.mladen.cikara.spring.hal.browser.learning.test.security.user;

import hr.mladen.cikara.spring.hal.browser.learning.test.security.register.RegisterDto;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

  /**
   * Finds all Users in repository.
   *
   * @param pageable Pagable object
   * @return Page with users
   */
  Page<User> findAll(Pageable pageable);

  /**
   * Get User by id.
   *
   * @param userId User id
   * @return User
   * @throws UserNotFoundException User with given id is not found
   */
  User findById(Long userId) throws UserNotFoundException;

  /**
   * Saves new user to repository.
   *
   * @param registerDto DTO with user data for registration
   */
  User register(RegisterDto registerDto) throws UsernameAlreadyTakenException, PasswordsDontMatch;

  /**
   * Get User by username.
   *
   * @param username Username
   * @return User
   * @throws UserNotFoundException User with given username is not found
   */
  User findByUsername(String username) throws UserNotFoundException;

  @Value
  @EqualsAndHashCode(callSuper = false)
  class UserNotFoundException extends Exception {
    private final Long userId;
    private final String username;

    UserNotFoundException(final String username) {
      this.username = username;
      this.userId = null;
    }

    UserNotFoundException(final Long userId) {
      this.username = null;
      this.userId = userId;
    }
  }

  @Value
  @EqualsAndHashCode(callSuper = false)
  class UsernameAlreadyTakenException extends Exception {
    private final String username;
  }

  class PasswordsDontMatch extends Exception {
  }
}
