package hr.mladen.cikara.spring.hal.browser.learning.test.security.register;

import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RegisterDto {
  @NotBlank
  private final String username;
  @NotBlank
  private final String password;
  @NotBlank
  private final String passwordRepeated;

}