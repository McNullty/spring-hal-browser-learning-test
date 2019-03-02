package hr.mladen.cikara.spring.hal.browser.learning.test.book;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@Getter(AccessLevel.PRIVATE)
@DisplayName("Given BookController")
class BookControllerPutSpecification extends AbstractBookControllerSpecification {
  public static final String TEST_AUTHOR_STRING = "Test author";
  public static final String TEST_TITLE_STRING = "Test title";
  public static final String TEST_BLURB_STRING = "Test blurb";
  public static final int PAGES_INTEGER = 190;
  public static final String BOOKS_URL = "/books/";
  public static final String AUTHOR = "author";
  public static final String TITLE = "title";
  public static final String BLURB = "blurb";
  public static final String PAGES = "pages";

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
                      .author(TEST_AUTHOR_STRING)
                      .title(TEST_TITLE_STRING)
                      .blurb(TEST_BLURB_STRING)
                      .pages(PAGES_INTEGER)
                      .build());

      Map<String, Object> book = createMapWithBookData();

      mockMvc.perform(
              RestDocumentationRequestBuilders.put(BOOKS_URL + savedBook.getId())
                      .content(objectMapper.writeValueAsString(book))
                      .accept(MediaType.APPLICATION_JSON_VALUE)
                      .contentType(MediaType.APPLICATION_JSON_VALUE))
              .andDo(MockMvcResultHandlers.print())
              .andExpect(MockMvcResultMatchers.status().isOk())
              .andExpect(
                      MockMvcResultMatchers.jsonPath(
                              AUTHOR, Matchers.is(book.get(AUTHOR))))
              .andExpect(
                      MockMvcResultMatchers.jsonPath(
                              TITLE, Matchers.is(book.get(TITLE))))
              .andExpect(
                      MockMvcResultMatchers.jsonPath(
                              BLURB, Matchers.is(book.get(BLURB))))
              .andExpect(
                      MockMvcResultMatchers.jsonPath(
                              PAGES, Matchers.is(book.get(PAGES))));
    }

    @DisplayName(
            "When trying to put book with all required fields filled and extra field, "
                    + "Then controller returns ok with book body, extra field is ignored")
    @Test
    void testPutExtraField() throws Exception {
      Book savedBook = bookRepository.save(
              new Book.BookBuilder()
                      .author(TEST_AUTHOR_STRING)
                      .title(TEST_TITLE_STRING)
                      .blurb(TEST_BLURB_STRING)
                      .pages(PAGES_INTEGER)
                      .build());

      Map<String, Object> book = createMapWithBookData();
      book.put("not-existing", "test");

      mockMvc.perform(
              RestDocumentationRequestBuilders.put(BOOKS_URL + savedBook.getId())
                      .content(objectMapper.writeValueAsString(book))
                      .accept(MediaType.APPLICATION_JSON_VALUE)
                      .contentType(MediaType.APPLICATION_JSON_VALUE))
              .andDo(MockMvcResultHandlers.print())
              .andExpect(MockMvcResultMatchers.status().isOk())
              .andExpect(
                      MockMvcResultMatchers.jsonPath(
                              AUTHOR, Matchers.is(book.get(AUTHOR))))
              .andExpect(
                      MockMvcResultMatchers.jsonPath(
                              TITLE, Matchers.is(book.get(TITLE))))
              .andExpect(
                      MockMvcResultMatchers.jsonPath(
                              BLURB, Matchers.is(book.get(BLURB))))
              .andExpect(
                      MockMvcResultMatchers.jsonPath(
                              PAGES, Matchers.is(book.get(PAGES))));
    }

    @DisplayName(
            "When trying to put book with some required fields empty, "
                    + "Then controller returns bad request with error message about "
                    + "validation errors")
    @Test
    void testPutInvalidBody() throws Exception {
      Book savedBook = bookRepository.save(
              new Book.BookBuilder()
                      .author(TEST_AUTHOR_STRING)
                      .title(TEST_TITLE_STRING)
                      .blurb(TEST_BLURB_STRING)
                      .pages(PAGES_INTEGER)
                      .build());

      Map<String, Object> book = createMapWithBookData();
      book.remove(AUTHOR);

      mockMvc.perform(
              RestDocumentationRequestBuilders.put(BOOKS_URL + savedBook.getId())
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
                      .author(TEST_AUTHOR_STRING)
                      .title(TEST_TITLE_STRING)
                      .blurb(TEST_BLURB_STRING)
                      .pages(PAGES_INTEGER)
                      .build());

      Map<String, Object> book = new HashMap<>();

      mockMvc.perform(
              RestDocumentationRequestBuilders.put(BOOKS_URL + savedBook.getId())
                      .content(objectMapper.writeValueAsString(book))
                      .accept(MediaType.APPLICATION_JSON_VALUE)
                      .contentType(MediaType.APPLICATION_JSON_VALUE))
              .andDo(MockMvcResultHandlers.print())
              .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @DisplayName(
            "When trying to put book with all required fields filled and blurb set to null , "
                    + "Then controller returns ok with book body")
    @Test
    void testPutValidBodyWithNullBlurb() throws Exception {
      Book savedBook = bookRepository.save(
              new Book.BookBuilder()
                      .author(TEST_AUTHOR_STRING)
                      .title(TEST_TITLE_STRING)
                      .blurb(TEST_BLURB_STRING)
                      .pages(PAGES_INTEGER)
                      .build());

      Map<String, Object> book = createMapWithBookData();
      book.put(BLURB, null);

      mockMvc.perform(
              RestDocumentationRequestBuilders.put(BOOKS_URL + savedBook.getId())
                      .content(objectMapper.writeValueAsString(book))
                      .accept(MediaType.APPLICATION_JSON_VALUE)
                      .contentType(MediaType.APPLICATION_JSON_VALUE))
              .andDo(MockMvcResultHandlers.print())
              .andExpect(MockMvcResultMatchers.status().isOk())
              .andExpect(
                      MockMvcResultMatchers.jsonPath(
                              AUTHOR, Matchers.is(book.get(AUTHOR))))
              .andExpect(
                      MockMvcResultMatchers.jsonPath(
                              TITLE, Matchers.is(book.get(TITLE))))
              .andExpect(
                      MockMvcResultMatchers.jsonPath(
                              BLURB, Matchers.is(Matchers.nullValue())))
              .andExpect(
                      MockMvcResultMatchers.jsonPath(
                              PAGES, Matchers.is(book.get(PAGES))));
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
              .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
  }
}