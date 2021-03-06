package hr.mladen.cikara.spring.hal.browser.learning.test.security.user;

import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
class ChangePasswordDto {
  @NotBlank
  private final String passwordRepeated;
  @NotBlank
  private final String password;
}
