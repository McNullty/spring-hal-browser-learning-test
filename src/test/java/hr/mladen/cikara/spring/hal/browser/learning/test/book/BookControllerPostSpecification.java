package hr.mladen.cikara.spring.hal.browser.learning.test.book;

import java.util.HashMap;
import java.util.Map;
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
    void testCreatingBookWithPayloadWithExtraField() throws Exception {
      mockMvc.perform(
              RestDocumentationRequestBuilders.post("/books")
                      .contentType(MediaType.APPLICATION_JSON_VALUE)
                      .content(objectMapper.writeValueAsString(book)))
              .andDo(MockMvcResultHandlers.print())
              .andExpect(MockMvcResultMatchers.status().isCreated())
              .andExpect(MockMvcResultMatchers.header().exists("Location"));
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
    void testCreatingBookWithPayloadWithExtraField() throws Exception {
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
