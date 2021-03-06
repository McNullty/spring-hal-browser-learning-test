package hr.mladen.cikara.spring.hal.browser.learning.test.book;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@Getter(AccessLevel.PRIVATE)
@SpringBootTest
abstract class AbstractBookControllerSpecification {

  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  WebApplicationContext webApplicationContext;

  @BeforeEach
  void setUp() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
            .build();
  }

  Map<String, Object> createMapWithBookData() {
    Map<String, Object> book = new HashMap<>();
    book.put("author", "Martin Fowler");
    book.put("title", "Refactoring: Improving the Design of Existing Code");
    book.put("blurb", "Any fool can write code that a computer can understand. "
            + "Good programmers write code that humans can understand.");
    book.put("pages", 448);

    return book;
  }
}
