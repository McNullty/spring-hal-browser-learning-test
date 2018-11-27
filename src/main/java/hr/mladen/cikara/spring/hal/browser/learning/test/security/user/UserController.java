package hr.mladen.cikara.spring.hal.browser.learning.test.security.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/users")
@ExposesResourceFor(User.class)
public class UserController {

  private final UserService userService;
  private final UserToUserResourceAssembler userToUserResourceAssembler;

  public UserController(final UserService userService, final UserToUserResourceAssembler userToUserResourceAssembler) {
    Assert.notNull(userService, "Controller can't work without user service");
    Assert.notNull(userToUserResourceAssembler, "Controller can't work without resource assembler");

    this.userService = userService;
    this.userToUserResourceAssembler = userToUserResourceAssembler;
  }

  @GetMapping(produces = {MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<PagedResources<UserResource>> findAll(
          final Pageable pageable,
          final PagedResourcesAssembler<User> assembler) {

    Page<User> users = userService.findAll(pageable);

    PagedResources<UserResource> userResourcePagedResources
            = getPagedUserResourcesWithLinks(assembler, users);

    return ResponseEntity.ok(userResourcePagedResources);
  }

  private PagedResources<UserResource> getPagedUserResourcesWithLinks(
          final PagedResourcesAssembler<User> assembler, final Page<User> users) {
    Link self = ControllerLinkBuilder.linkTo(UserController.class).withSelfRel();

    return assembler.toResource(users, userToUserResourceAssembler, self);
  }

  @RequestMapping(value = "/user", method = RequestMethod.POST)
  public User create(@RequestBody User user){
    return userService.save(user);
  }

  @RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
  public String delete(@PathVariable(value = "id") Long id){
    userService.delete(id);
    return "success";
  }

}
