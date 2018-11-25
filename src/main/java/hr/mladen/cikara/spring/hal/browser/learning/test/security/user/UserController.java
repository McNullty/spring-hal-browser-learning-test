package hr.mladen.cikara.spring.hal.browser.learning.test.security.user;

import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  @Autowired
  public UserController(final UserService userService) {
    Assert.notNull(userService, "Controller can't work without user service");
    this.userService = userService;
  }

  @RequestMapping(value="/user", method = RequestMethod.GET)
  public Collection<User> listUser(){
    return userService.findAll();
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
