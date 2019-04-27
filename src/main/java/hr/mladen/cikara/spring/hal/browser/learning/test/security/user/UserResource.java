package hr.mladen.cikara.spring.hal.browser.learning.test.security.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.core.Relation;

@Data
@EqualsAndHashCode(callSuper = false)
@Relation(value = "user", collectionRelation = "users")
public class UserResource extends ResourceSupport {
  private final String username;

  private final String firstName;

  private final String lastName;

  UserResource(User user) {
    this.username = user.getUsername();
    this.firstName = user.getFirstName();
    this.lastName = user.getLastName();
  }
}
