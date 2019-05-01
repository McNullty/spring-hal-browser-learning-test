package hr.mladen.cikara.spring.hal.browser.learning.test.security.user;

import java.util.Collection;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resources;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Getter(AccessLevel.PRIVATE)
@RestController
@RequestMapping("/users/{userId}/authorities")
@ExposesResourceFor(UserAuthority.class)
public class UserAuthorityController {

  private final UserAuthorityService userAuthorityService;

  public UserAuthorityController(final UserAuthorityService userAuthorityService) {
    this.userAuthorityService = userAuthorityService;
  }

  /**
   * Returns list of user authorities for user.
   *
   * @param userId User Id.
   * @return List of authorities
   * @throws UserService.UserNotFoundException If user is not found.
   */
  @GetMapping(produces = {MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<Resources<UserAuthorityResource>> findAllForUser(
          @PathVariable final Long userId) throws UserService.UserNotFoundException {
    Collection<UserAuthority> userAuthorities =
            userAuthorityService.findAllAuthoritiesForUserId(userId);

    Resources<UserAuthorityResource> userAuthorityResources =
            convertUserAuthoritiesToResources(userAuthorities);

    return ResponseEntity.ok(userAuthorityResources);
  }

  private Resources<UserAuthorityResource> convertUserAuthoritiesToResources(
          final Collection<UserAuthority> userAuthorities) {

    return new Resources<>(
            userAuthorities.stream()
                    .map(UserAuthorityResource::new)
                    .collect(Collectors.toList()));
  }
}
