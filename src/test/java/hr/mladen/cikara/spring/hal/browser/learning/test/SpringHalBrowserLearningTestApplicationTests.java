package hr.mladen.cikara.spring.hal.browser.learning.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DisplayName("Smoke test for spring context")
@ExtendWith(SpringExtension.class)
@SpringBootTest
class SpringHalBrowserLearningTestApplicationTests {

  @DisplayName("Starting Spring context")
  @Test
  void contextLoads() {
  }

}
