package hr.mladen.cikara.spring.hal.browser.learning.test.security.user;

import java.util.Collection;

public interface UserAuthorityService {
  Collection<UserAuthority> findAllAuthoritiesForUserId(Long userId)
          throws UserService.UserNotFoundException;

  void deleteAuthority(Long userId, String toUpperCase) throws UserService.UserNotFoundException;
}
