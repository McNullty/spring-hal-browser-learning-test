package hr.mladen.cikara.spring.hal.browser.learning.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;

@SpringBootApplication(exclude = {ErrorMvcAutoConfiguration.class})
public class SpringHalBrowserLearningTestApplication {

  public static void main(final String[] args) {
    SpringApplication.run(SpringHalBrowserLearningTestApplication.class, args);
  }
}
