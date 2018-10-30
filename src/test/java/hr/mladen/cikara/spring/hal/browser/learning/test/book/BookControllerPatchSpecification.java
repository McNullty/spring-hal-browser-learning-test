package hr.mladen.cikara.spring.hal.browser.learning.test.book;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@DisplayName("Given BookController")
class BookControllerPatchSpecification extends AbstractBookControllerSpecification {

  @Autowired
  BookRepository bookRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @DisplayName("and valid book id")
  @Nested
  class ValidBookId {

    @DisplayName(
            "When trying to patch book, "
                    + "Then controller returns ok with book body")
    @Test
    void testPatchingBook() throws Exception {
      Book savedBook = bookRepository.save(
              new Book.BookBuilder()
                      .author("Test author")
                      .title("Test title")
                      .blurb("Test blurb")
                      .pages(190)
                      .build());

      Map<String, Object> book = createMapWithBookData();
      book.remove("author");

      mockMvc.perform(
              RestDocumentationRequestBuilders.patch("/books/" + savedBook.getId())
                      .content(objectMapper.writeValueAsString(book))
                      .accept(MediaType.APPLICATION_JSON_VALUE)
                      .contentType(MediaType.APPLICATION_JSON_VALUE))
              .andDo(MockMvcResultHandlers.print())
              .andExpect(MockMvcResultMatchers.status().isOk())
              .andExpect(
                      MockMvcResultMatchers.jsonPath(
                              "author", Matchers.is(savedBook.getAuthor())))
              .andExpect(
                      MockMvcResultMatchers.jsonPath(
                              "title", Matchers.is(book.get("title"))))
              .andExpect(
                      MockMvcResultMatchers.jsonPath(
                              "blurb", Matchers.is(book.get("blurb"))))
              .andExpect(
                      MockMvcResultMatchers.jsonPath(
                              "pages", Matchers.is(book.get("pages"))));
    }
  }

  @DisplayName("and invalid book id")
  @Nested
  class InvalidBookId {
    @DisplayName(
            "When trying to get book, Then controller returns not found")
    @Test
    void testPatchingBook() throws Exception {
      Map<String, Object> book = createMapWithBookData();
      book.remove("author");

      mockMvc.perform(
              RestDocumentationRequestBuilders.patch("/books/99999")
                      .content(objectMapper.writeValueAsString(book))
                      .accept(MediaType.APPLICATION_JSON_VALUE)
                      .contentType(MediaType.APPLICATION_JSON_VALUE))
              .andDo(MockMvcResultHandlers.print())
              .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
  }
}
