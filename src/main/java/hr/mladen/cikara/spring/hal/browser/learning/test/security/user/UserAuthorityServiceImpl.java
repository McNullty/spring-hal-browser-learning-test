package hr.mladen.cikara.spring.hal.browser.learning.test.security.user;

import java.util.Collection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Getter(AccessLevel.PRIVATE)
@Service(value = "userAuthorityService")
public class UserAuthorityServiceImpl implements UserAuthorityService {

  private final UserAuthorityRepository userAuthorityRepository;

  public UserAuthorityServiceImpl(final UserAuthorityRepository userAuthorityRepository) {
    this.userAuthorityRepository = userAuthorityRepository;
  }

  @Override
  public Collection<UserAuthority> findAllAuthoritiesForUserId(final Long userId) {
    return userAuthorityRepository.findAllUserAuthorityByUserId(userId);
  }
}
