package hr.mladen.cikara.spring.hal.browser.learning.test.documentation;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

public class AbstractDocumentationWithSecurity {
  MockMvc mockMvc;

  /**
   * Setting up documentation tests.
   *
   * @param webApplicationContext test application context
   * @param restDocumentation documentation context provider
   */
  @BeforeEach
  void setUp(final WebApplicationContext webApplicationContext,
             final RestDocumentationContextProvider restDocumentation) {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(MockMvcRestDocumentation.documentationConfiguration(restDocumentation))
            .apply(SecurityMockMvcConfigurers.springSecurity())
            .build();
  }
}
