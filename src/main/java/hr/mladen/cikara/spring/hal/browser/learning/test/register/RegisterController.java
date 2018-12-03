package hr.mladen.cikara.spring.hal.browser.learning.test.register;

import hr.mladen.cikara.spring.hal.browser.learning.test.security.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class RegisterController {

  private final UserService userService;

  public RegisterController(final UserService userService) {
    this.userService = userService;
  }

  @PostMapping(path = "/register",
          consumes = {MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<?> register(RegisterDto registerDto) {
    return null;
  }
}
