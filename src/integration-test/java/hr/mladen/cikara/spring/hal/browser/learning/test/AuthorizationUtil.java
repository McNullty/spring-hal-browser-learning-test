package hr.mladen.cikara.spring.hal.browser.learning.test;

import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Utility class for getting authorization string.
 */
@Getter(AccessLevel.PRIVATE)
public class AuthorizationUtil {

  public static final String PASSWORD_LITERAL = "password";
  private final MockMvc mockMvc;

  public AuthorizationUtil(final MockMvc mockMvc) {
    this.mockMvc = mockMvc;
  }

  /**
   * Sends authorization data and returns response from oauth2 server.
   *
   * @param username Username for authorisation
   * @param password Password for user
   * @return Authorization response
   * @throws Exception mockMvc can return exception
   */
  public String getAccessTokenFromAuthorizationResponse(
          String username, String password) throws Exception {
    MultiValueMap<String, String> loginParams = new LinkedMultiValueMap<>();
    loginParams.add("grant_type", PASSWORD_LITERAL);
    loginParams.add("username", username);
    loginParams.add(PASSWORD_LITERAL, password);

    String resultString = this.mockMvc.perform(
            MockMvcRequestBuilders.post("/oauth/token")
                    .params(loginParams)
                    .with(SecurityMockMvcRequestPostProcessors.httpBasic(
                            "application-client", PASSWORD_LITERAL))
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(MockMvcResultHandlers.print())
            .andReturn().getResponse().getContentAsString();

    JacksonJsonParser jsonParser = new JacksonJsonParser();
    return jsonParser.parseMap(resultString).get("access_token").toString();
  }

  /**
   * Sends authorization data and returns response from oauth2 server.
   *
   * @param username Username for authorisation
   * @param password Password for user
   * @return Authorization response
   * @throws Exception mockMvc can return exception
   */
  public String getAuthorizationResponse(
          String username, String password) throws Exception {
    MultiValueMap<String, String> loginParams = new LinkedMultiValueMap<>();
    loginParams.add("grant_type", PASSWORD_LITERAL);
    loginParams.add("username", username);
    loginParams.add(PASSWORD_LITERAL, password);

    return this.mockMvc.perform(
            MockMvcRequestBuilders.post("/oauth/token")
                    .params(loginParams)
                    .with(SecurityMockMvcRequestPostProcessors.httpBasic(
                            "application-client", PASSWORD_LITERAL))
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(MockMvcResultHandlers.print())
            .andReturn().getResponse().getContentAsString();
  }
}
