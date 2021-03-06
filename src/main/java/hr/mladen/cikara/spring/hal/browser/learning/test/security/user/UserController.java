package hr.mladen.cikara.spring.hal.browser.learning.test.security.user;

import java.security.Principal;
import javax.validation.Valid;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Getter(AccessLevel.PRIVATE)
@RestController
@RequestMapping("/users")
@ExposesResourceFor(User.class)
public class UserController {

  private final UserService userService;
  private final UserToUserResourceAssembler userToUserResourceAssembler;

  /**
   * Controller for displaying user resources.
   *
   * @param userService                 UserService
   * @param userToUserResourceAssembler Assembler to convert form Zser to UserResource
   */
  public UserController(
          final UserService userService,
          final UserToUserResourceAssembler userToUserResourceAssembler) {
    Assert.notNull(userService, "Controller can't work without user service");
    Assert.notNull(userToUserResourceAssembler, "Controller can't work without resource assembler");

    this.userService = userService;
    this.userToUserResourceAssembler = userToUserResourceAssembler;
  }

  /**
   * Returns all users.
   *
   * @param pageable  Pageable object
   * @param assembler Assembler to convert form Zser to UserResource
   * @return Page with UserResources
   */
  @GetMapping(produces = {MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<PagedResources<UserResource>> findAll(
          final Pageable pageable,
          final PagedResourcesAssembler<User> assembler) {

    Page<User> users = userService.findAll(pageable);

    PagedResources<UserResource> userResourcePagedResources
            = getPagedUserResourcesWithLinks(assembler, users);

    return ResponseEntity.ok(userResourcePagedResources);
  }

  private PagedResources<UserResource> getPagedUserResourcesWithLinks(
          final PagedResourcesAssembler<User> assembler, final Page<User> users) {
    Link self = ControllerLinkBuilder.linkTo(UserController.class).withSelfRel();

    return assembler.toResource(users, userToUserResourceAssembler, self);
  }

  /**
   * Returns User resource for given id.
   *
   * @param userId User id
   * @return UserResource
   * @throws UserService.UserNotFoundException when user with give ID is not found
   */
  @GetMapping(
          value = "/{userId}",
          produces = {MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<UserResource> getUser(@PathVariable final Long userId)
          throws UserService.UserNotFoundException {
    User user = userService.findById(userId);

    UserResource userResource = userToUserResourceAssembler.toResource(user);

    return ResponseEntity.ok(userResource);
  }

  /**
   * Returns currently authenticated user.
   *
   * @param principal Autowired principal
   * @return UserResource
   */
  @GetMapping(
          value = "/me",
          produces = {MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<UserResource> getCurrentUser(Principal principal) {
    log.debug("Principal: {}", principal);

    try {
      User user = userService.findByUsername(principal.getName());

      UserResource userResource = userToUserResourceAssembler.toResource(user);

      return ResponseEntity.ok(userResource);
    } catch (UserService.UserNotFoundException e) {
      log.error("This should never happen! Current user should always be found.");

      throw new RuntimeException("Current user not found!");
    }
  }

  /**
   * Endpoint for changing user password. User can change password for himself.
   *
   * @param changePasswordDto DTO with new password
   * @param principal         Logged in principal
   * @return HTTP status No Content
   */
  @PutMapping(
          value = "/me/change-password",
          consumes = {MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<?> changePassword(
          @Valid @RequestBody ChangePasswordDto changePasswordDto, Principal principal)
          throws UserService.PasswordsDontMatch, UserService.UserNotFoundException {

    userService.changePassword(principal.getName(), changePasswordDto);

    return ResponseEntity.noContent().build();
  }
}
