package hr.mladen.cikara.spring.hal.browser.learning.test.book;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@DisplayName("Given BookController")
@ExtendWith({SpringExtension.class})
@SpringBootTest
class BookControllerSpecification {

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

  @DisplayName("and invalid payload with missing author")
  @Nested
  class InvalidPayload {

    Map<String, Object> book;

    @BeforeEach
    void setup() {
      book = new HashMap<>();
      book.put("title", "Refactoring: Improving the Design of Existing Code");
      book.put("blurb", "Any fool can write code that a computer can understand. "
              + "Good programmers write code that humans can understand.");
      book.put("pages", 448);
    }

    @DisplayName(
            "When trying to create new book, controller returns bad request with error description")
    @Test
    void testCreatingBookWithInvalidPayload() throws Exception {
      mockMvc.perform(
              post("/books").contentType(MediaTypes.HAL_JSON).content(
                      objectMapper.writeValueAsString(book)))
              .andDo(print())
              .andExpect(status().isBadRequest());
    }
  }

  @DisplayName("and payload with field that is not any of DTO fields (\"test\": \"bad\")")
  @Nested
  class PayloadWithFieldThatCanNotBeMapped {
    Map<String, Object> book;

    @BeforeEach
    void setup() {
      book = new HashMap<>();
      book.put("author", "Martin Fowler");
      book.put("title", "Refactoring: Improving the Design of Existing Code");
      book.put("blurb", "Any fool can write code that a computer can understand. "
              + "Good programmers write code that humans can understand.");
      book.put("pages", 448);
      book.put("test", "bad");
    }

    @DisplayName(
            "When trying to create new book, controller returns Created and extra field is ignored")
    @Test
    void testCreatingBookWithPayloadWithExtraField() throws Exception {
      mockMvc.perform(
              post("/books").contentType(MediaTypes.HAL_JSON).content(
                      objectMapper.writeValueAsString(book)))
              .andDo(print())
              .andExpect(status().isCreated());
    }
  }
}
