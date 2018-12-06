package hr.mladen.cikara.spring.hal.browser.learning.test.security.register;

import hr.mladen.cikara.spring.hal.browser.learning.test.security.user.UserService;
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

  public RegisterController(final UserService userService) {
    this.userService = userService;
  }

  /**
   * Endpoint for registering new user.
   *
   * @param registerDto DTO with data for new user
   * @return HTTP status NoContent
   */
  @PostMapping(path = "/register",
          consumes = {MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<?> register(@Valid @RequestBody RegisterDto registerDto) {
    log.debug("Got RegisterDto: {}", registerDto);

    userService.register(registerDto);

    return ResponseEntity.noContent().build();
  }
}
