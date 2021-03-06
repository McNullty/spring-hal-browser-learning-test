package hr.mladen.cikara.spring.hal.browser.learning.test.security.user;

import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

@Getter(AccessLevel.PRIVATE)
@Component
public class UserToUserResourceAssembler implements ResourceAssembler<User, UserResource> {
  private final EntityLinks entityLinks;

  public UserToUserResourceAssembler(final EntityLinks entityLinks) {
    this.entityLinks = entityLinks;
  }

  @Override
  public UserResource toResource(final User entity) {
    UserResource userResource = new UserResource(entity);
    userResource.add(entityLinks.linkToSingleResource(User.class, entity.getId()).withSelfRel());
    userResource.add(entityLinks.linkFor(
            UserAuthority.class, entity.getId()).withRel("userAuthorities"));

    return userResource;
  }
}
