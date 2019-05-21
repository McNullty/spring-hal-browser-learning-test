package hr.mladen.cikara.spring.hal.browser.learning.test.security.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.core.Relation;

@Data
@EqualsAndHashCode(callSuper = false)
@Relation(value = "authority", collectionRelation = "authorities")
class UserAuthorityResource extends ResourceSupport {
  private final String authority;

  UserAuthorityResource(UserAuthority userAuthority) {
    this.authority = userAuthority.getAuthority();
  }
}
