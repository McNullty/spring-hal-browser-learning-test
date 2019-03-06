package hr.mladen.cikara.spring.hal.browser.learning.test.documentation;

import lombok.AccessLevel;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

@Getter(AccessLevel.PRIVATE)
@ExtendWith({RestDocumentationExtension.class})
@SpringBootTest
class AbstractDocumentationWithSecurity {
  public static final String PASSWORD_STRING = "password";
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

  /**
   * Sends authorization data and returns response from oauth2 server.
   *
   * @return Authorization response
   * @throws Exception mockMvc can return exception
   */
  String getAuthorizationResponse() throws Exception {
    MultiValueMap<String, String> loginParams = new LinkedMultiValueMap<>();
    loginParams.add("grant_type", PASSWORD_STRING);
    loginParams.add("username", "Alex123");
    loginParams.add(PASSWORD_STRING, PASSWORD_STRING);

    return this.mockMvc.perform(
            MockMvcRequestBuilders.post("/oauth/token")
                    .params(loginParams)
                    .with(SecurityMockMvcRequestPostProcessors.httpBasic(
                            "application-client", PASSWORD_STRING))
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(MockMvcResultHandlers.print())
            .andReturn().getResponse().getContentAsString();
  }
}
