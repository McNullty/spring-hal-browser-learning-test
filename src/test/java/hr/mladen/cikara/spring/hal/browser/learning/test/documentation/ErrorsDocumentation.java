package hr.mladen.cikara.spring.hal.browser.learning.test.documentation;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.servlet.RequestDispatcher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ErrorsDocumentation extends AbstractDocumentation {

  @Test
  @DisplayName("Documentation for /error endpoint")
  public void errorExample() throws Exception {
    this.mockMvc
            .perform(get("/error")
                    .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 400)
                    .requestAttr(RequestDispatcher.ERROR_REQUEST_URI,
                            "/books")
                    .requestAttr(RequestDispatcher.ERROR_MESSAGE,
                            "The book 'http://localhost:8080/books/123' does not exist"))
            .andDo(print()).andExpect(status().isBadRequest())
            .andExpect(jsonPath("error", is("Bad Request")))
            .andExpect(jsonPath("timestamp", is(notNullValue())))
            .andExpect(jsonPath("status", is(400)))
            .andExpect(jsonPath("path", is(notNullValue())))
            .andDo(document("error-example",
                    responseFields(
                            fieldWithPath("error")
                                    .description("The HTTP error that occurred, e.g. "
                                            + "`Bad Request`"),
                            fieldWithPath("message")
                                    .description("A description of the cause of the error"),
                            fieldWithPath("path")
                                    .description("The path to which the request was made"),
                            fieldWithPath("status")
                                    .description("The HTTP status code, e.g. `400`"),
                            fieldWithPath("timestamp")
                                    .description("The time, in milliseconds, at which the "
                                            + "error occurred"))));
  }

}
