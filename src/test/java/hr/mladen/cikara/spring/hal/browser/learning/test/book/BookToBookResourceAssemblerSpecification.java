package hr.mladen.cikara.spring.hal.browser.learning.test.book;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.Link;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DisplayName("Given Spring Context and example book")
@ExtendWith(SpringExtension.class)
@SpringBootTest
class BookToBookResourceAssemblerSpecification {

  @Autowired
  private BookToBookResourceAssembler bookToBookResourceAssembler;
  private Book book;

  @BeforeEach
  void setup() {
    book = new Book();
    book.setId(1);
    book.setAuthor("Test Author");
    book.setTitle("Test Title");
    book.setBlurb("Test Blurb");
    book.setPages(369);
  }

  @Nested
  @DisplayName("When calling toResource on BookToBookResourceAssembler")
  class WhenCallingToResource {

    @DisplayName("Then expect BookResource object")
    @Test
    void testToResource() {
      BookResource resource = bookToBookResourceAssembler.toResource(book);

      Assertions.assertThat(resource.getAuthor()).isEqualTo(book.getAuthor());
      Assertions.assertThat(resource.getTitle()).isEqualTo(book.getTitle());
      Assertions.assertThat(resource.getBlurb()).isEqualTo(book.getBlurb());
      Assertions.assertThat(resource.getPages()).isEqualTo(book.getPages());
      Assertions.assertThat(resource.getLink("self"))
              .isEqualTo(new Link("http://localhost/books/" + book.getId()));
    }
  }

}
