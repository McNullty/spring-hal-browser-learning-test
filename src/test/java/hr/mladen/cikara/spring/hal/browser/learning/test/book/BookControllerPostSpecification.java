package hr.mladen.cikara.spring.hal.browser.learning.test.book;

import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@DisplayName("Given BookController")
class BookControllerPostSpecification extends AbstractBookControllerSpecification {

  public static final String BOOKS_URL = "/books";

  @Getter(AccessLevel.PRIVATE)
  @DisplayName("and invalid payload with missing author, when creating new book")
  @Nested
  class InvalidPayload {

    Map<String, Object> book;

    @BeforeEach
    void setUp() {
      book = createMapWithBookData();
      book.remove("author");
    }

    @DisplayName(
            "Then controller returns bad request with error description")
    @Test
    void testCreatingBookWithInvalidPayload() throws Exception {
      mockMvc.perform(
              RestDocumentationRequestBuilders.post(BOOKS_URL)
                      .contentType(MediaType.APPLICATION_JSON_VALUE)
                      .content(objectMapper.writeValueAsString(book)))
              .andDo(MockMvcResultHandlers.print())
              .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
  }

  @Getter(AccessLevel.PRIVATE)
  @DisplayName("and payload with field that is not any of DTO fields (\"test\": \"bad\"), "
          + "when creating new book")
  @Nested
  class PayloadWithFieldThatCanNotBeMapped {
    Map<String, Object> book;

    @BeforeEach
    void setUp() {
      book = createMapWithBookData();
      book.put("test", "bad");
    }

    @DisplayName("Then controller returns Created and extra field is ignored")
    @Test
    void testCreatingBookWithPayloadWithExtraField() throws Exception {
      mockMvc.perform(
              RestDocumentationRequestBuilders.post(BOOKS_URL)
                      .contentType(MediaType.APPLICATION_JSON_VALUE)
                      .content(objectMapper.writeValueAsString(book)))
              .andDo(MockMvcResultHandlers.print())
              .andExpect(MockMvcResultMatchers.status().isCreated());
    }
  }

  @Getter(AccessLevel.PRIVATE)
  @DisplayName("and valid payload, when creating new book")
  @Nested
  class NormalPayload {
    Map<String, Object> book;

    @BeforeEach
    void setUp() {
      book = createMapWithBookData();
    }

    @DisplayName("Then controller returns Created with Location in header")
    @Test
    void testCreatingBookWithValidPayload() throws Exception {
      mockMvc.perform(
              RestDocumentationRequestBuilders.post(BOOKS_URL)
                      .contentType(MediaType.APPLICATION_JSON_VALUE)
                      .content(objectMapper.writeValueAsString(book)))
              .andDo(MockMvcResultHandlers.print())
              .andExpect(MockMvcResultMatchers.status().isCreated())
              .andExpect(MockMvcResultMatchers.header().exists("Location"));
    }

    @DisplayName(
            "with blurb set to null, "
                    + "Then controller returns Created with Location in header")
    @Test
    void testCreatingBookWithValidPayloadBlurbSetToNull() throws Exception {
      book.put("blurb", null);

      String bookLocation = mockMvc.perform(
              RestDocumentationRequestBuilders.post(BOOKS_URL)
                      .contentType(MediaType.APPLICATION_JSON_VALUE)
                      .content(objectMapper.writeValueAsString(book)))
              .andDo(MockMvcResultHandlers.print())
              .andExpect(MockMvcResultMatchers.status().isCreated())
              .andExpect(MockMvcResultMatchers.header().exists("Location"))
              .andReturn().getResponse().getHeader("Location");

      if (bookLocation == null) {
        Assert.fail();
      }

      mockMvc.perform(RestDocumentationRequestBuilders.get(bookLocation))
              .andDo(MockMvcResultHandlers.print())
              .andExpect(MockMvcResultMatchers.status().isOk())
              .andExpect(MockMvcResultMatchers.jsonPath("title", Matchers.is(book.get("title"))))
              .andExpect(MockMvcResultMatchers.jsonPath("author", Matchers.is(book.get("author"))))
              .andExpect(MockMvcResultMatchers.jsonPath("blurb", Matchers.is(Matchers.nullValue())))
              .andExpect(MockMvcResultMatchers.jsonPath("pages", Matchers.is(book.get("pages"))))
              .andExpect(MockMvcResultMatchers.jsonPath("_links.self.href", Matchers.is(bookLocation)));
    }
  }

  @Getter(AccessLevel.PRIVATE)
  @DisplayName("and unsupported ContentType, when creating new book")
  @Nested
  class WrongContentType {
    Map<String, Object> book;

    @BeforeEach
    void setUp() {
      book = createMapWithBookData();
    }

    @DisplayName("Then controller returns Unsupported Media Type")
    @Test
    void testCreatingBookWithValidPayload() throws Exception {
      mockMvc.perform(
              RestDocumentationRequestBuilders.post(BOOKS_URL)
                      .contentType(MediaType.TEXT_PLAIN_VALUE)
                      .content(objectMapper.writeValueAsString(book)))
              .andDo(MockMvcResultHandlers.print())
              .andExpect(MockMvcResultMatchers.status().isUnsupportedMediaType());
    }
  }

  @Getter(AccessLevel.PRIVATE)
  @DisplayName("and empty payload, when creating new book")
  @Nested
  class EmptyPayload {
    Map<String, Object> book = new HashMap<>();

    @DisplayName("Then controller returns Bad Request")
    @Test
    void testCreatingBookWithPayloadWithExtraField() throws Exception {
      mockMvc.perform(
              RestDocumentationRequestBuilders.post(BOOKS_URL)
                      .contentType(MediaType.APPLICATION_JSON_VALUE)
                      .content(objectMapper.writeValueAsString(book)))
              .andDo(MockMvcResultHandlers.print())
              .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
  }
}
