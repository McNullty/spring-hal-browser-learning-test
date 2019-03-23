package hr.mladen.cikara.spring.hal.browser.learning.test;

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
public class AuthorizationUtil {

  // TODO: Autowire MockMvc on initialization if possible

  /**
   * Sends authorization data and returns response from oauth2 server.
   *
   * @param mockMvc MockMvc object that is passed form test
   * @param username Username for authorisation
   * @param password Passord for user
   * @return Authorization response
   * @throws Exception mockMvc can return exception
   */
  public static String getAuthorizationResponse(
          MockMvc mockMvc, String username, String password) throws Exception {
    MultiValueMap<String, String> loginParams = new LinkedMultiValueMap<>();
    loginParams.add("grant_type", "password");
    loginParams.add("username", username);
    loginParams.add("password", password);

    String resultString = mockMvc.perform(
            MockMvcRequestBuilders.post("/oauth/token")
                    .params(loginParams)
                    .with(SecurityMockMvcRequestPostProcessors.httpBasic(
                            "application-client", "password"))
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(MockMvcResultHandlers.print())
            .andReturn().getResponse().getContentAsString();

    JacksonJsonParser jsonParser = new JacksonJsonParser();
    return jsonParser.parseMap(resultString).get("access_token").toString();
  }
}
