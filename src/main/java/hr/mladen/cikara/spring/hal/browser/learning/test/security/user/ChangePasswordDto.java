package hr.mladen.cikara.spring.hal.browser.learning.test.security.user;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;

@Value
@Builder
class ChangePasswordDto {
  @NotBlank
  private final String passwordRepeated;
  @NotBlank
  private final String password;
}
