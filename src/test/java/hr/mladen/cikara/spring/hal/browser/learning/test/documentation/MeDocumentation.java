package hr.mladen.cikara.spring.hal.browser.learning.test.documentation;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

class MeDocumentation extends AbstractDocumentationWithSecurity {

  @Test
  void meExample() throws Exception {
    String jsonResponse = getAuthorizationResponse();

    JSONObject jsonObject = new JSONObject(jsonResponse);

    this.mockMvc.perform(
            MockMvcRequestBuilders.get("/users/me")
                    .header("Authorization", "Bearer " +
                            jsonObject.get("access_token")))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
    ;
  }
}
