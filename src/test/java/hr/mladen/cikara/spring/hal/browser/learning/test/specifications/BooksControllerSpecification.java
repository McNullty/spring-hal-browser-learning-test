package hr.mladen.cikara.spring.hal.browser.learning.test.specifications;

import hr.mladen.cikara.spring.hal.browser.learning.test.book.BooksController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@WebMvcTest(BooksController.class)
@DisplayName("Given BooksController")
public class BooksControllerSpecification {
  @Autowired
  private MockMvc mockMvc;
}
