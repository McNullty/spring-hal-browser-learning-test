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

  private final UserService userService;

  public UserAuthorityServiceImpl(
          final UserAuthorityRepository userAuthorityRepository, final UserService userService) {
    this.userAuthorityRepository = userAuthorityRepository;
    this.userService = userService;
  }

  @Override
  public Collection<UserAuthority> findAllAuthoritiesForUserId(final Long userId)
          throws UserService.UserNotFoundException {
    userService.findById(userId);

    return userAuthorityRepository.findAllUserAuthorityByUserId(userId);
  }

  @Override
  public void deleteAuthority(
          final Long userId, final String toUpperCase)
          throws UserService.UserNotFoundException, UserAuthorityNotFoundException {

  }
}
