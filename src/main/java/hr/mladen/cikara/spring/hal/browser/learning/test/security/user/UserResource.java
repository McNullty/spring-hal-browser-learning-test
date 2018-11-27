package hr.mladen.cikara.spring.hal.browser.learning.test.security.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.core.Relation;

@Data
@EqualsAndHashCode(callSuper = false)
@Relation(value = "user", collectionRelation = "users")
class UserResource extends ResourceSupport {
  private final String username;

  UserResource(User user) {
    this.username = user.getUsername();
  }
}
