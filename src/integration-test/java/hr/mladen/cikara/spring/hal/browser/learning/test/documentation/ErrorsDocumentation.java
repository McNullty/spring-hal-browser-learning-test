package hr.mladen.cikara.spring.hal.browser.learning.test.documentation;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ErrorsDocumentation extends AbstractDocumentation {

  @Test
  @DisplayName("Documentation for error message")
  void errorExample() throws Exception {
    this.mockMvc
            .perform(get("/books/700"))
            .andDo(print()).andExpect(status().isNotFound())
            .andExpect(jsonPath("status", is("NOT_FOUND")))
            .andExpect(jsonPath("timestamp", is(notNullValue())))
            .andExpect(jsonPath("message", is("Couldn't find book with id 700")))
            .andExpect(jsonPath("debugMessage", is(nullValue())))
            .andExpect(jsonPath("subErrors", is(nullValue())))
            .andDo(document("error-example",
                    responseFields(
                            fieldWithPath("status")
                                    .description("The HTTP error that occurred, e.g. "
                                            + "`Bad Request`"),
                            fieldWithPath("message")
                                    .description("A description of the cause of the error"),
                            fieldWithPath("debugMessage").type("String")
                                    .description("Debug message"),
                            fieldWithPath("subErrors").type("String")
                                    .description("List of validation errors with descriptions"),
                            fieldWithPath("timestamp").type("Timestamp")
                                    .description("The time, in milliseconds with timezone, at "
                                            + "which the error occurred"))));

  }

}
