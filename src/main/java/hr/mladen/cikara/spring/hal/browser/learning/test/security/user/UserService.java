package hr.mladen.cikara.spring.hal.browser.learning.test.security.user;

import hr.mladen.cikara.spring.hal.browser.learning.test.security.register.RegisterDto;

import java.util.Collection;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
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

  /**
   * Changes password for user with given userId.
   *
   * @param username Username for user that will have his password changed
   * @param changePasswordDto DTO with new password
   */
  void changePassword(String username, ChangePasswordDto changePasswordDto)
          throws PasswordsDontMatch, UserNotFoundException;

  Collection<UserAuthority> findAllAuthoritiesForUserId(Long userId)
          throws UserService.UserNotFoundException;

  void deleteAuthority(Long userId, String userAuthority)
          throws UserService.UserNotFoundException, UserAuthorityNotFoundException;

  @Getter(AccessLevel.PUBLIC)
  @EqualsAndHashCode(callSuper = false)
  class UserNotFoundException extends Exception {
    private static final long serialVersionUID = 1832746950175216234L;

    private Long userId = null;
    private String username = null;

    UserNotFoundException(final String username) {
      this.username = username;
    }

    UserNotFoundException(final Long userId) {
      this.userId = userId;
    }
  }

  @Value
  @EqualsAndHashCode(callSuper = false)
  class UsernameAlreadyTakenException extends Exception {
    private static final long serialVersionUID = -3784779833611983483L;

    private final String username;
  }

  class PasswordsDontMatch extends Exception {
    private static final long serialVersionUID = -5232027232632055140L;
  }

  @Value
  @EqualsAndHashCode(callSuper = false)
  class UserAuthorityNotFoundException extends Exception {
    private static final long serialVersionUID = -1196898544769707935L;

    private final Long userId;
    private final String userAuthority;
  }
}
