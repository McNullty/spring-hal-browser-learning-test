package hr.mladen.cikara.spring.hal.browser.learning.test.book;

import java.util.HashMap;
import java.util.Map;
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

  @DisplayName("and invalid payload with missing author")
  @Nested
  class InvalidPayload {

    Map<String, Object> book;

    @BeforeEach
    void setup() {
      book = createMapWithBookData();
      book.remove("author");
    }

    @DisplayName(
            "When trying to create new book, "
                    + "Then controller returns bad request with error description")
    @Test
    void testCreatingBookWithInvalidPayload() throws Exception {
      mockMvc.perform(
              RestDocumentationRequestBuilders.post("/books")
                      .contentType(MediaType.APPLICATION_JSON_VALUE)
                      .content(objectMapper.writeValueAsString(book)))
              .andDo(MockMvcResultHandlers.print())
              .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
  }

  @DisplayName("and payload with field that is not any of DTO fields (\"test\": \"bad\")")
  @Nested
  class PayloadWithFieldThatCanNotBeMapped {
    Map<String, Object> book;

    @BeforeEach
    void setup() {
      book = createMapWithBookData();
      book.put("test", "bad");
    }

    @DisplayName(
            "When trying to create new book, "
                    + "Then controller returns Created and extra field is ignored")
    @Test
    void testCreatingBookWithPayloadWithExtraField() throws Exception {
      mockMvc.perform(
              RestDocumentationRequestBuilders.post("/books")
                      .contentType(MediaType.APPLICATION_JSON_VALUE)
                      .content(objectMapper.writeValueAsString(book)))
              .andDo(MockMvcResultHandlers.print())
              .andExpect(MockMvcResultMatchers.status().isCreated());
    }
  }

  @DisplayName("and valid payload")
  @Nested
  class NormalPayload {
    Map<String, Object> book;

    @BeforeEach
    void setup() {
      book = createMapWithBookData();
    }

    @DisplayName(
            "When trying to create new book, "
                    + "Then controller returns Created with Location in header")
    @Test
    void testCreatingBookWithValidPayload() throws Exception {
      mockMvc.perform(
              RestDocumentationRequestBuilders.post("/books")
                      .contentType(MediaType.APPLICATION_JSON_VALUE)
                      .content(objectMapper.writeValueAsString(book)))
              .andDo(MockMvcResultHandlers.print())
              .andExpect(MockMvcResultMatchers.status().isCreated())
              .andExpect(MockMvcResultMatchers.header().exists("Location"));
    }

    @DisplayName(
            "When trying to create new book with blurb set to null, "
                    + "Then controller returns Created with Location in header")
    @Test
    void testCreatingBookWithValidPayloadBlurbSetToNull() throws Exception {
      book.put("blurb", null);

      String bookLocation = mockMvc.perform(
              RestDocumentationRequestBuilders.post("/books")
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

  @DisplayName("and unsupported ContentType")
  @Nested
  class WrongContentType {
    Map<String, Object> book;

    @BeforeEach
    void setup() {
      book = createMapWithBookData();
    }

    @DisplayName(
            "When trying to create new book, "
                    + "Then controller returns Unsupported Media Type")
    @Test
    void testCreatingBookWithValidPayload() throws Exception {
      mockMvc.perform(
              RestDocumentationRequestBuilders.post("/books")
                      .contentType(MediaType.TEXT_PLAIN_VALUE)
                      .content(objectMapper.writeValueAsString(book)))
              .andDo(MockMvcResultHandlers.print())
              .andExpect(MockMvcResultMatchers.status().isUnsupportedMediaType());
    }
  }

  @DisplayName("and empty payload")
  @Nested
  class EmptyPayload {
    Map<String, Object> book = new HashMap<>();

    @DisplayName(
            "When trying to create new book, "
                    + "Then controller returns Bad Request")
    @Test
    void testCreatingBookWithPayloadWithExtraField() throws Exception {
      mockMvc.perform(
              RestDocumentationRequestBuilders.post("/books")
                      .contentType(MediaType.APPLICATION_JSON_VALUE)
                      .content(objectMapper.writeValueAsString(book)))
              .andDo(MockMvcResultHandlers.print())
              .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
  }
}
