package hr.mladen.cikara.spring.hal.browser.learning.test.documentation;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

class MeDocumentation extends AbstractDocumentation {

  @Test
  void meExample() throws Exception {
    String authorization = this.authorizationUtil.getAccessTokenFromAuthorizationResponse(
            "Alex123", "password");

    this.mockMvc.perform(
            MockMvcRequestBuilders.get("/users/me")
                    .header("Authorization", "Bearer "
                            + authorization))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo(document("me-example",
                    responseFields(
                            fieldWithPath("username").description("Currently authenticated user."),
                            fieldWithPath("firstName").description("Users first name"),
                            fieldWithPath("lastName").description("Users last name"),
                            subsectionWithPath("_links")
                                    .description("The <<resources-users,Users resource>> "
                                            + "to other resources"))))
    ;
  }
}
