package hr.mladen.cikara.spring.hal.browser.learning.test.security.user;

import java.util.Collection;

import lombok.EqualsAndHashCode;
import lombok.Value;

public interface UserAuthorityService {
  Collection<UserAuthority> findAllAuthoritiesForUserId(Long userId)
          throws UserService.UserNotFoundException;

  void deleteAuthority(Long userId, String toUpperCase)
          throws UserService.UserNotFoundException, UserAuthorityNotFoundException;

  @Value
  @EqualsAndHashCode(callSuper = false)
  class UserAuthorityNotFoundException extends Exception {
    private static final long serialVersionUID = -1196898544769707935L;

    private final Long userId;
    private final String userAuthority;
  }
}
