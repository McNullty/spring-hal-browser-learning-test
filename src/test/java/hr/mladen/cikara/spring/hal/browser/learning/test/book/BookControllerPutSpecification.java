package hr.mladen.cikara.spring.hal.browser.learning.test.book;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
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
class BookControllerPutSpecification extends AbstractBookControllerSpecification {
  @Autowired
  BookRepository bookRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @DisplayName("and valid book id")
  @Nested
  class ValidId {

    @DisplayName(
            "When trying to put book with all required fields filled, "
                    + "Then controller returns ok with book body")
    @Test
    void testPutValidBody() throws Exception {
      Book savedBook = bookRepository.save(
              new Book.BookBuilder()
                      .author("Test author")
                      .title("Test title")
                      .blurb("Test blurb")
                      .pages(190)
                      .build());

      Map<String, Object> book = createMapWithBookData();

      mockMvc.perform(
              RestDocumentationRequestBuilders.put("/books/" + savedBook.getId())
                      .content(objectMapper.writeValueAsString(book))
                      .accept(MediaType.APPLICATION_JSON_VALUE)
                      .contentType(MediaType.APPLICATION_JSON_VALUE))
              .andDo(MockMvcResultHandlers.print())
              .andExpect(MockMvcResultMatchers.status().isOk())
              .andExpect(
                      MockMvcResultMatchers.jsonPath(
                              "author", Matchers.is(book.get("author"))))
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

    @DisplayName(
            "When trying to put book with all required fields filled and extra field, "
                    + "Then controller returns ok with book body, extra field is ignored")
    @Test
    void testPutExtraField() throws Exception {
      Book savedBook = bookRepository.save(
              new Book.BookBuilder()
                      .author("Test author")
                      .title("Test title")
                      .blurb("Test blurb")
                      .pages(190)
                      .build());

      Map<String, Object> book = createMapWithBookData();
      book.put("not-existing", "test");

      mockMvc.perform(
              RestDocumentationRequestBuilders.put("/books/" + savedBook.getId())
                      .content(objectMapper.writeValueAsString(book))
                      .accept(MediaType.APPLICATION_JSON_VALUE)
                      .contentType(MediaType.APPLICATION_JSON_VALUE))
              .andDo(MockMvcResultHandlers.print())
              .andExpect(MockMvcResultMatchers.status().isOk())
              .andExpect(
                      MockMvcResultMatchers.jsonPath(
                              "author", Matchers.is(book.get("author"))))
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

    @DisplayName(
            "When trying to put book with some required fields empty, "
                    + "Then controller returns bad request with error message about "
                    + "validation errors")
    @Test
    void testPutInvalidBody() throws Exception {
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
              RestDocumentationRequestBuilders.put("/books/" + savedBook.getId())
                      .content(objectMapper.writeValueAsString(book))
                      .accept(MediaType.APPLICATION_JSON_VALUE)
                      .contentType(MediaType.APPLICATION_JSON_VALUE))
              .andDo(MockMvcResultHandlers.print())
              .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @DisplayName(
            "When trying to put book with empty body, "
                    + "Then controller returns bad request with error message about "
                    + "validation errors")
    @Test
    void testPutEmptyBody() throws Exception {
      Book savedBook = bookRepository.save(
              new Book.BookBuilder()
                      .author("Test author")
                      .title("Test title")
                      .blurb("Test blurb")
                      .pages(190)
                      .build());

      Map<String, Object> book = new HashMap<>();

      mockMvc.perform(
              RestDocumentationRequestBuilders.put("/books/" + savedBook.getId())
                      .content(objectMapper.writeValueAsString(book))
                      .accept(MediaType.APPLICATION_JSON_VALUE)
                      .contentType(MediaType.APPLICATION_JSON_VALUE))
              .andDo(MockMvcResultHandlers.print())
              .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
  }

  @DisplayName("and invalid book id")
  @Nested
  class InvalidBookId {
    @DisplayName(
            "When trying to put book with all required fields filled, "
                    + "Then controller returns bad request with instruction to use POST endpoint")
    @Test
    void testPutValidBody() throws Exception {

      Map<String, Object> book = createMapWithBookData();

      mockMvc.perform(
              RestDocumentationRequestBuilders.put("/books/999")
                      .content(objectMapper.writeValueAsString(book))
                      .accept(MediaType.APPLICATION_JSON_VALUE)
                      .contentType(MediaType.APPLICATION_JSON_VALUE))
              .andDo(MockMvcResultHandlers.print())
              .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
  }
}