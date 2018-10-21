package hr.mladen.cikara.spring.hal.browser.learning.test.links;

import hr.mladen.cikara.spring.hal.browser.learning.test.book.BooksController;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class})
@DisplayName("Given LinkExtractor")
class LinkExtractorSpecification {

  private LinkExtractor linkExtractor;

  @Nested
  @DisplayName("When BooksController is given")
  class WhenBooksController {
    @BeforeEach
    void setup() {
      linkExtractor = new LinkExtractor(BooksController.class);
    }

    @Test
    @DisplayName("link name should be books")
    void testExtractingLinkName() {
      Link link = linkExtractor.getLink();

      Assertions.assertThat(link.getName()).isEqualTo("books");
    }

    @Test
    @DisplayName("link href should be /books{?page,size,sort}")
    void testExtractingLinkHref() {
      Link link = linkExtractor.getLink();

      Assertions.assertThat(link.getHref())
              .isEqualTo("/books{?page,size,sort}");
    }

    @Test
    @DisplayName("link templated should be true")
    void testExtractingLinkTemplated() {
      Link link = linkExtractor.getLink();

      Assertions.assertThat(link.getTemplated())
              .isEqualTo(true);
    }
  }
}
