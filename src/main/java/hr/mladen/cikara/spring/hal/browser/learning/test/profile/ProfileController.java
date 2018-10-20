package hr.mladen.cikara.spring.hal.browser.learning.test.profile;

import java.util.Arrays;
import org.springframework.hateoas.alps.Alps;
import org.springframework.hateoas.alps.Descriptor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("profile")
public class ProfileController {

  @RequestMapping(value = "books", method = RequestMethod.GET, produces = {"application/hal+json"})
  public ResponseEntity<?> booksProfile() {

    Alps alps = Alps.alps().descriptors(
            Arrays.asList(Descriptor.builder().name("test").build())).build();
    return ResponseEntity.ok(alps);
  }
}
