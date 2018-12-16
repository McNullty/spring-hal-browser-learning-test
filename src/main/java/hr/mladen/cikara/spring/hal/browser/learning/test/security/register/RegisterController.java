package hr.mladen.cikara.spring.hal.browser.learning.test.security.register;

import hr.mladen.cikara.spring.hal.browser.learning.test.security.user.User;
import hr.mladen.cikara.spring.hal.browser.learning.test.security.user.UserResource;
import hr.mladen.cikara.spring.hal.browser.learning.test.security.user.UserService;
import hr.mladen.cikara.spring.hal.browser.learning.test.security.user.UserToUserResourceAssembler;
import java.net.URI;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class RegisterController {

  private final UserService userService;
  private final UserToUserResourceAssembler userToUserResourceAssembler;

  public RegisterController(
          final UserService userService,
          final UserToUserResourceAssembler userToUserResourceAssembler) {
    this.userService = userService;
    this.userToUserResourceAssembler = userToUserResourceAssembler;
  }

  /**
   * Endpoint for registering new user.
   *
   * @param registerDto DTO with data for new user
   * @return HTTP status NoContent
   */
  @PostMapping(path = "/register",
          consumes = {MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<?> register(@Valid @RequestBody RegisterDto registerDto)
          throws UserService.UsernameAlreadyTakenException, UserService.PasswordsDontMatch {
    log.debug("Got RegisterDto: {}", registerDto);

    User user = userService.register(registerDto);

    UserResource userResource = userToUserResourceAssembler.toResource(user);

    return ResponseEntity
            .created(URI.create(userResource.getLink("self").getHref()))
            .build();
  }
}
