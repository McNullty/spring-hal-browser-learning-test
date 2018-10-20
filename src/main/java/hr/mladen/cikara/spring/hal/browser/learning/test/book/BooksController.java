package hr.mladen.cikara.spring.hal.browser.learning.test.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("books")
public class BooksController {

  private final BookRepository bookRepository;

  public BooksController(final BookRepository bookRepository) {
    this.bookRepository = bookRepository;
  }

  @RequestMapping(method = RequestMethod.GET, produces = {"application/hal+json"})
  ResponseEntity<Page<Book>> findAll(Pageable pageable) {

    return ResponseEntity.ok(bookRepository.findAll(pageable));
  }
}
