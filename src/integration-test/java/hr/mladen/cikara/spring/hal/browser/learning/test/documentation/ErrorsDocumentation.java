package hr.mladen.cikara.spring.hal.browser.learning.test.documentation;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

class ErrorsDocumentation extends AbstractDocumentation {

  @Test
  @DisplayName("Documentation for error message")
  void errorExample() throws Exception {
    String authorization = this.authorizationUtil.getAccessTokenFromAuthorizationResponse(
            "Alex123", "password");

    this.mockMvc
            .perform(RestDocumentationRequestBuilders.get("/books/700")
                    .header("Authorization", "Bearer " + authorization))
            .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isNotFound())
            .andExpect(MockMvcResultMatchers.jsonPath("status", Matchers.is("NOT_FOUND")))
            .andExpect(MockMvcResultMatchers.jsonPath("timestamp", Matchers.is(Matchers.notNullValue())))
            .andExpect(MockMvcResultMatchers.jsonPath("message", Matchers.is("Couldn't find book with id 700")))
            .andExpect(MockMvcResultMatchers.jsonPath("debugMessage", Matchers.is(Matchers.nullValue())))
            .andExpect(MockMvcResultMatchers.jsonPath("subErrors", Matchers.is(Matchers.nullValue())))
            .andDo(MockMvcRestDocumentation.document("error-example",
                    PayloadDocumentation.responseFields(
                            PayloadDocumentation.fieldWithPath("status")
                                    .description("The HTTP error that occurred, e.g. "
                                            + "`Bad Request`"),
                            PayloadDocumentation.fieldWithPath("message")
                                    .description("A description of the cause of the error"),
                            PayloadDocumentation.fieldWithPath("debugMessage").type("String")
                                    .description("Debug message"),
                            PayloadDocumentation.fieldWithPath("subErrors").type("String")
                                    .description("List of validation errors with descriptions"),
                            PayloadDocumentation.fieldWithPath("timestamp").type("Timestamp")
                                    .description("The time, in milliseconds with timezone, at "
                                            + "which the error occurred"))));

  }

}
