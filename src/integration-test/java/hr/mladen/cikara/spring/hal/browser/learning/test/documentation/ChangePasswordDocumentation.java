package hr.mladen.cikara.spring.hal.browser.learning.test.documentation;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import hr.mladen.cikara.spring.hal.browser.learning.test.AuthorizationUtil;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@Getter(AccessLevel.PRIVATE)
@DisplayName("Documentation for /users/me/change-password endpoint")
class ChangePasswordDocumentation extends AbstractDocumentation {

  public static final String PASSWORD_LITERAL = "password";
  @Autowired
  private ObjectMapper objectMapper;

  private AuthorizationUtil authorizationUtil;

  @BeforeEach
  void setUp() {
    authorizationUtil = new AuthorizationUtil(this.mockMvc);
  }

  @Test
  void changePasswordExample() throws Exception {
    Map<String, Object> changePasswordDto = new HashMap<>();
    changePasswordDto.put(PASSWORD_LITERAL, PASSWORD_LITERAL);
    changePasswordDto.put("passwordRepeated", PASSWORD_LITERAL);

    String authorization = authorizationUtil.getAccessTokenFromAuthorizationResponse(
            "Alex123", PASSWORD_LITERAL);

    this.mockMvc.perform(
            MockMvcRequestBuilders.put("/users/me/change-password")
                    .header("Authorization", "Bearer " + authorization)
                    .contentType(MediaTypes.HAL_JSON)
                    .content(this.objectMapper.writeValueAsString(changePasswordDto)))
            .andExpect(status().isNoContent())
            .andDo(document("change-password-example",
                    requestFields(
                            fieldWithPath(PASSWORD_LITERAL)
                                    .description("Password for new <<resources-users,user>>"),
                            fieldWithPath("passwordRepeated").description("Repeated password"))));
  }
}
