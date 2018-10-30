package hr.mladen.cikara.spring.hal.browser.learning.test.book;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
class BookControllerPostSpecification {

  @Autowired
  ObjectMapper objectMapper;
  @Autowired
  WebApplicationContext webApplicationContext;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
            .build();
  }

  private Map<String, Object> createMapWithBookData() {
    Map<String, Object> book = new HashMap<>();
    book.put("author", "Martin Fowler");
    book.put("title", "Refactoring: Improving the Design of Existing Code");
    book.put("blurb", "Any fool can write code that a computer can understand. "
            + "Good programmers write code that humans can understand.");
    book.put("pages", 448);

    return book;
  }

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
                    + "Then controller returns Created")
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
