package hr.mladen.cikara.spring.hal.browser.learning.test.book;

import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@DisplayName("Given BookController")
class BookControllerDeleteSpecification extends AbstractBookControllerSpecification {

  @Autowired
  BookRepository bookRepository;

  @DisplayName("and valid book id")
  @Nested
  class ValidBookId {

    @DisplayName(
            "When trying to delete book, "
                    + "Then controller returns no content")
    @Test
    void testGettingBookJson() throws Exception {
      Book savedBook = bookRepository.save(
              new Book.BookBuilder()
                      .author("Test author")
                      .title("Test title")
                      .blurb("Test blurb")
                      .pages(190)
                      .build());

      mockMvc.perform(
              RestDocumentationRequestBuilders.delete("/books/" + savedBook.getId())
                      .accept(MediaType.APPLICATION_JSON_VALUE))
              .andDo(MockMvcResultHandlers.print())
              .andExpect(MockMvcResultMatchers.status().isNoContent());

      Optional<Book> loadBook = bookRepository.findById(savedBook.getId());
      Assertions.assertThat(loadBook.isPresent()).isFalse();
    }
  }

  @DisplayName("and invalid book id")
  @Nested
  class InvalidBookId {
    @DisplayName(
            "When trying to get book, Then controller returns not found")
    @Test
    void testGettingBook() throws Exception {
      mockMvc.perform(
              RestDocumentationRequestBuilders.delete("/books/99999")
                      .accept(MediaType.APPLICATION_JSON_VALUE))
              .andDo(MockMvcResultHandlers.print())
              .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
  }
}
