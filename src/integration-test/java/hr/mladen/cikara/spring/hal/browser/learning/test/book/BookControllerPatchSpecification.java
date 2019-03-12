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
class BookControllerPatchSpecification extends AbstractBookControllerSpecification {

  public static final String TEST_AUTHOR_STRING = "Test author";
  public static final String TEST_TITLE_STRING = "Test title";
  public static final String TEST_BLURB_STRING = "Test blurb";
  public static final String AUTHOR = "author";
  public static final String TITLE = "title";
  public static final String BLURB = "blurb";
  public static final String PAGES = "pages";
  public static final String BOOKS_URL = "/books/";
  public static final int PAGES_INTEGER = 190;

  @Autowired
  BookRepository bookRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @DisplayName("and valid book id")
  @Nested
  class ValidBookId {

    @DisplayName(
            "When trying to patch book with valid body, "
                    + "Then controller returns ok with book body")
    @Test
    void testPatchingBookValidBody() throws Exception {
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
              RestDocumentationRequestBuilders.patch(BOOKS_URL + savedBook.getId())
                      .content(objectMapper.writeValueAsString(book))
                      .accept(MediaType.APPLICATION_JSON_VALUE)
                      .contentType(MediaType.APPLICATION_JSON_VALUE))
              .andDo(MockMvcResultHandlers.print())
              .andExpect(MockMvcResultMatchers.status().isOk())
              .andExpect(
                      MockMvcResultMatchers.jsonPath(
                              AUTHOR, Matchers.is(savedBook.getAuthor())))
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
            "When trying to patch book with valid body and all class field are set, "
                    + "Then controller returns ok with book body")
    @Test
    void testPatchingBookValidBodyAllFields() throws Exception {
      Book savedBook = bookRepository.save(
              new Book.BookBuilder()
                      .author(TEST_AUTHOR_STRING)
                      .title(TEST_TITLE_STRING)
                      .blurb(TEST_BLURB_STRING)
                      .pages(PAGES_INTEGER)
                      .build());

      Map<String, Object> book = createMapWithBookData();

      mockMvc.perform(
              RestDocumentationRequestBuilders.patch(BOOKS_URL + savedBook.getId())
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
            "When trying to patch book with not existing field, "
                    + "Then controller returns ok with book body, no fields are updated")
    @Test
    void testPatchingBookNotExistingField() throws Exception {
      Book savedBook = bookRepository.save(
              new Book.BookBuilder()
                      .author(TEST_AUTHOR_STRING)
                      .title(TEST_TITLE_STRING)
                      .blurb(TEST_BLURB_STRING)
                      .pages(PAGES_INTEGER)
                      .build());

      Map<String, Object> book = new HashMap<>();
      book.put("not-existing", "test");

      mockMvc.perform(
              RestDocumentationRequestBuilders.patch(BOOKS_URL + savedBook.getId())
                      .content(objectMapper.writeValueAsString(book))
                      .accept(MediaType.APPLICATION_JSON_VALUE)
                      .contentType(MediaType.APPLICATION_JSON_VALUE))
              .andDo(MockMvcResultHandlers.print())
              .andExpect(MockMvcResultMatchers.status().isOk())
              .andExpect(
                      MockMvcResultMatchers.jsonPath(
                              AUTHOR, Matchers.is(savedBook.getAuthor())))
              .andExpect(
                      MockMvcResultMatchers.jsonPath(
                              TITLE, Matchers.is(savedBook.getTitle())))
              .andExpect(
                      MockMvcResultMatchers.jsonPath(
                              BLURB, Matchers.is(savedBook.getBlurb())))
              .andExpect(
                      MockMvcResultMatchers.jsonPath(
                              PAGES, Matchers.is(savedBook.getPages())));
    }

    @DisplayName(
            "When trying to patch book with empty body, "
                    + "Then controller returns ok with book body, no fields are updated")
    @Test
    void testPatchingBookEmptyBody() throws Exception {
      Book savedBook = bookRepository.save(
              new Book.BookBuilder()
                      .author(TEST_AUTHOR_STRING)
                      .title(TEST_TITLE_STRING)
                      .blurb(TEST_BLURB_STRING)
                      .pages(PAGES_INTEGER)
                      .build());

      Map<String, Object> book = new HashMap<>();

      mockMvc.perform(
              RestDocumentationRequestBuilders.patch(BOOKS_URL + savedBook.getId())
                      .content(objectMapper.writeValueAsString(book))
                      .accept(MediaType.APPLICATION_JSON_VALUE)
                      .contentType(MediaType.APPLICATION_JSON_VALUE))
              .andDo(MockMvcResultHandlers.print())
              .andExpect(MockMvcResultMatchers.status().isOk())
              .andExpect(
                      MockMvcResultMatchers.jsonPath(
                              AUTHOR, Matchers.is(savedBook.getAuthor())))
              .andExpect(
                      MockMvcResultMatchers.jsonPath(
                              TITLE, Matchers.is(savedBook.getTitle())))
              .andExpect(
                      MockMvcResultMatchers.jsonPath(
                              BLURB, Matchers.is(savedBook.getBlurb())))
              .andExpect(
                      MockMvcResultMatchers.jsonPath(
                              PAGES, Matchers.is(savedBook.getPages())));
    }

    @DisplayName(
            "When trying to patch book with blurb set to null, "
                    + "Then controller returns ok with book body, blurb is updated to null")
    @Test
    void testPatchBookSetBlurbToNull() throws Exception {
      Book savedBook = bookRepository.save(
              new Book.BookBuilder()
                      .author(TEST_AUTHOR_STRING)
                      .title(TEST_TITLE_STRING)
                      .blurb(TEST_BLURB_STRING)
                      .pages(PAGES_INTEGER)
                      .build());

      Map<String, Object> book = new HashMap<>();
      book.put(BLURB, null);

      mockMvc.perform(
              RestDocumentationRequestBuilders.patch(BOOKS_URL + savedBook.getId())
                      .content(objectMapper.writeValueAsString(book))
                      .accept(MediaType.APPLICATION_JSON_VALUE)
                      .contentType(MediaType.APPLICATION_JSON_VALUE))
              .andDo(MockMvcResultHandlers.print())
              .andExpect(MockMvcResultMatchers.status().isOk())
              .andExpect(
                      MockMvcResultMatchers.jsonPath(
                              AUTHOR, Matchers.is(savedBook.getAuthor())))
              .andExpect(
                      MockMvcResultMatchers.jsonPath(
                              TITLE, Matchers.is(savedBook.getTitle())))
              .andExpect(
                      MockMvcResultMatchers.jsonPath(
                              BLURB, Matchers.is(Matchers.nullValue())))
              .andExpect(
                      MockMvcResultMatchers.jsonPath(
                              PAGES, Matchers.is(savedBook.getPages())));
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
      book.remove(AUTHOR);

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
