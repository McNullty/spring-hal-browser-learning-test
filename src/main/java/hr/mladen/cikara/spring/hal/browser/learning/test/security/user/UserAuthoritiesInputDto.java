package hr.mladen.cikara.spring.hal.browser.learning.test.security.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import lombok.Builder;
import lombok.Value;

@Value
public class UserAuthoritiesInputDto {

  @NotEmpty
  private final List<UserAuthorityEnum> userAuthorities;

  @JsonCreator
  @Builder
  public UserAuthoritiesInputDto(
          @JsonProperty("userAuthorities") List<UserAuthorityEnum> userAuthorities) {
    this.userAuthorities = userAuthorities;
  }
}
