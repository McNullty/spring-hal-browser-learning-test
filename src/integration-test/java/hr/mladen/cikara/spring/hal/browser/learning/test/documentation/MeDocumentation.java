package hr.mladen.cikara.spring.hal.browser.learning.test.documentation;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
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
                    .header("Authorization", "Bearer "
                            + jsonObject.get("access_token")))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo(document("me-example",
                    responseFields(
                            fieldWithPath("username").description("Currently authenticated user."),
                            subsectionWithPath("_links")
                                    .description("The <<resources-users,Users resource>> "
                                            + "to other resources"))))
    ;
  }
}
