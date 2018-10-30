package hr.mladen.cikara.spring.hal.browser.learning.test.book;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@DisplayName("Given BookController")
@ExtendWith({SpringExtension.class})
@SpringBootTest
class BookControllerGetBookSpecification {

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  WebApplicationContext webApplicationContext;

  @Autowired
  BookRepository bookRepository;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
            .build();
  }

  @DisplayName("and valid book id")
  @Nested
  class ValidBookId {

    @DisplayName(
            "When trying to get book with acceptType application/json, "
                    + "Then controller returns ok with book data")
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
              RestDocumentationRequestBuilders.get("/books/" + savedBook.getId())
                      .accept(MediaType.APPLICATION_JSON_VALUE))
              .andDo(MockMvcResultHandlers.print())
              .andExpect(MockMvcResultMatchers.status().isOk())
              .andExpect(
                      MockMvcResultMatchers.jsonPath(
                              "author", Matchers.is(savedBook.getAuthor())))
              .andExpect(
                      MockMvcResultMatchers.jsonPath(
                              "title", Matchers.is(savedBook.getTitle())))
              .andExpect(
                      MockMvcResultMatchers.jsonPath(
                              "blurb", Matchers.is(savedBook.getBlurb())))
              .andExpect(
                      MockMvcResultMatchers.jsonPath(
                              "pages", Matchers.is(savedBook.getPages())));
    }

    @DisplayName(
            "When trying to get book with acceptType application/hal+json, "
                    + "Then controller returns ok with book data")
    @Test
    void testGettingBookHalAndJson() throws Exception {
      Book savedBook = bookRepository.save(
              new Book.BookBuilder()
                      .author("Test author")
                      .title("Test title")
                      .blurb("Test blurb")
                      .pages(190)
                      .build());

      mockMvc.perform(
              RestDocumentationRequestBuilders.get("/books/" + savedBook.getId())
                      .accept(MediaTypes.HAL_JSON_VALUE))
              .andDo(MockMvcResultHandlers.print())
              .andExpect(MockMvcResultMatchers.status().isOk())
              .andExpect(
                      MockMvcResultMatchers.jsonPath(
                              "author", Matchers.is(savedBook.getAuthor())))
              .andExpect(
                      MockMvcResultMatchers.jsonPath(
                              "title", Matchers.is(savedBook.getTitle())))
              .andExpect(
                      MockMvcResultMatchers.jsonPath(
                              "blurb", Matchers.is(savedBook.getBlurb())))
              .andExpect(
                      MockMvcResultMatchers.jsonPath(
                              "pages", Matchers.is(savedBook.getPages())));
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
              RestDocumentationRequestBuilders.get("/books/99999")
                      .accept(MediaType.APPLICATION_JSON_VALUE))
              .andDo(MockMvcResultHandlers.print())
              .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
  }
}
