package hr.mladen.cikara.spring.hal.browser.learning.test.documentation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.PayloadDocumentation;
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


@ExtendWith({RestDocumentationExtension.class})
@SpringBootTest
class AuthorizationDocumentation {

  private MockMvc mockMvc;

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

  @Test
  void authorizationExample() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("grant_type", "password");
    params.add("username", "Alex123");
    params.add("password", "password");


    this.mockMvc.perform(
            MockMvcRequestBuilders.post("/oauth/token")
                    .params(params)
                    .with(SecurityMockMvcRequestPostProcessors.httpBasic(
                            "application-client", "password"))
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(MockMvcRestDocumentation.document("authorize-example",
//                    RequestDocumentation.pathParameters(
//                            RequestDocumentation.parameterWithName("grant_type")
//                                    .description("Authorization type"),
//                            RequestDocumentation.parameterWithName("client_id")
//                                    .description("Web Client Id"),
//                            RequestDocumentation.parameterWithName("username")
//                                    .description("Username"),
//                            RequestDocumentation.parameterWithName("password")
//                                    .description("Password")),
                    PayloadDocumentation.responseFields(
                            PayloadDocumentation.fieldWithPath("access_token")
                                    .description("Access Token"),
                            PayloadDocumentation.fieldWithPath("token_type")
                                    .description("Token type"),
                            PayloadDocumentation.fieldWithPath("refresh_token")
                                    .description("Refresh token"),
                            PayloadDocumentation.fieldWithPath("expires_in")
                                    .description("Time to expire token in seconds"),
                            PayloadDocumentation.fieldWithPath("scope")
                                    .description("List of scopes"),
                            PayloadDocumentation.fieldWithPath("jti")
                                    .description("JTI")
                            )))
    ;
  }
}
