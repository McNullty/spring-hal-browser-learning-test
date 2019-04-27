package hr.mladen.cikara.spring.hal.browser.learning.test.security.user;

import java.util.Collection;

public interface UserAuthorityService {
  Collection<UserAuthority> findAllAuthoritiesForUserId(Long userId);
}
