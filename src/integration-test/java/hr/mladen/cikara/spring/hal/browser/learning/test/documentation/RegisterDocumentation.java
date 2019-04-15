package hr.mladen.cikara.spring.hal.browser.learning.test.documentation;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@Getter(AccessLevel.PRIVATE)
@DisplayName("Documentation for /users/register endpoint")
class RegisterDocumentation extends AbstractDocumentation {

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("Documentation for register endpoint")
  void registerExample() throws Exception {

    Map<String, Object> registerDto = new HashMap<>();
    registerDto.put("username", "TestUser");
    registerDto.put("password", "TestPassword123");
    registerDto.put("passwordRepeated", "TestPassword123");

    this.mockMvc.perform(
            MockMvcRequestBuilders.post("/users/register")
                    .contentType(MediaTypes.HAL_JSON)
                    .content(this.objectMapper.writeValueAsString(registerDto)))
            .andExpect(status().isCreated())
            .andDo(document("register-example",
                    requestFields(
                            fieldWithPath("username").description("Username for registration"),
                            fieldWithPath("password")
                                    .description("Password for new <<resources-users,user>>"),
                            fieldWithPath("passwordRepeated").description("Repeated password"))));
  }
}
