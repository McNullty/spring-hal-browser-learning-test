package hr.mladen.cikara.spring.hal.browser.learning.test.security.register;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonDeserialize(builder = RegisterDto.RegisterDtoBuilder.class)
public class RegisterDto {

  private final String username;
  private final String password;
  private final String passwordRepeated;

  @JsonPOJOBuilder(withPrefix = "")
  static final class RegisterDtoBuilder{}
}
