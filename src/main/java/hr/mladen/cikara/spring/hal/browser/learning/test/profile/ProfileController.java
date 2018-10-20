package hr.mladen.cikara.spring.hal.browser.learning.test.profile;

import hr.mladen.cikara.spring.hal.browser.learning.test.book.Book;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("profile")
public class ProfileController {

  @RequestMapping(value = "books", method = RequestMethod.GET, produces = {"application/hal+json"})
  public ResponseEntity<Book> booksProfile() {
    return ResponseEntity.ok(null);
  }
}
