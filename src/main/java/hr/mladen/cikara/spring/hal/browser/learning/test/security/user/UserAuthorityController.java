package hr.mladen.cikara.spring.hal.browser.learning.test.security.user;

import java.net.URI;
import java.util.Collection;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.validation.Valid;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resources;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Getter(AccessLevel.PRIVATE)
@RestController
@RequestMapping("/users/{userId}/authorities")
@ExposesResourceFor(UserAuthority.class)
public class UserAuthorityController {

  private final UserService userService;
  private final EntityLinks entityLinks;

  public UserAuthorityController(
          final UserService userService, final EntityLinks entityLinks) {
    this.userService = userService;
    this.entityLinks = entityLinks;
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
            userService.findAllAuthoritiesForUserId(userId);

    Resources<UserAuthorityResource> userAuthorityResources =
            convertUserAuthoritiesToResources(userAuthorities);

    return ResponseEntity.ok(userAuthorityResources);
  }

  /**
   * Endpoint for removing user authority.
   *
   * @param userId User Id.
   * @param authority User Authority that is to be removed.
   * @return No content
   * @throws UserService.UserNotFoundException If user with userId doesn't exist
   * @throws UserService.UserAuthorityNotFoundException if user authority doesnt exist
   */
  @DeleteMapping(
          value = "/{authority}",
          produces = {MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<?> removeUserAuthority(
          @PathVariable final Long userId, @PathVariable final String authority)
          throws UserService.UserNotFoundException,
          UserService.UserAuthorityNotFoundException {
    log.debug("Trying to delete authority {}", authority.toUpperCase(Locale.getDefault()));

    userService.deleteAuthority(userId, authority.toUpperCase(Locale.getDefault()));

    return ResponseEntity.noContent().build();
  }

  /**
   * Endpoint for adding user authorities.
   *
   * @param userId User Id.
   * @param userAuthorities List of user authorities to be added.
   * @return Created response with link to list of user authorities
   * @throws UserService.UserNotFoundException If user with userId doesn't exist
   */
  @PostMapping(
          consumes = {MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE}
  )
  public ResponseEntity<?> addUserAuthorities(
          @PathVariable final Long userId,
          @Valid @RequestBody final UserAuthoritiesInputDto userAuthorities)
          throws UserService.UserNotFoundException {

    userService.addUserAuthorities(userId, userAuthorities.getUserAuthorities());

    final URI uri = entityLinks.linkFor(UserAuthority.class, userId).toUri();

    return ResponseEntity.created(uri).build();
  }

  private Resources<UserAuthorityResource> convertUserAuthoritiesToResources(
          final Collection<UserAuthority> userAuthorities) {

    return new Resources<>(
            userAuthorities.stream()
                    .map(UserAuthorityResource::new)
                    .collect(Collectors.toList()));
  }
}
