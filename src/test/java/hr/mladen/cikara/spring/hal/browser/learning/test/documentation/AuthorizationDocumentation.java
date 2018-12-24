package hr.mladen.cikara.spring.hal.browser.learning.test.documentation;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.headers.HeaderDocumentation;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

class AuthorizationDocumentation extends AbstractDocumentationWithSecurity {

  @Test
  void authorizationExample() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("grant_type", "password");
    params.add("username", "Alex123");
    params.add("password", "password");

    this.mockMvc.perform(
            MockMvcRequestBuilders.post("/oauth/token")
                    .params(params)
                    .with(SecurityMockMvcRequestPostProcessors.httpBasic(
                            "application-client", "password"))
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(MockMvcRestDocumentation.document("authorize-example",
                    RequestDocumentation.requestParameters(
                            RequestDocumentation.parameterWithName("grant_type")
                                    .description("This tells the server we’re using the Password "
                                            + "grant type."),
                            RequestDocumentation.parameterWithName("username")
                                    .description("The user’s username that they entered in the "
                                            + "application."),
                            RequestDocumentation.parameterWithName("password")
                                    .description("The user’s password that they entered in the "
                                            + "application.")),
                    HeaderDocumentation.requestHeaders(
                            HeaderDocumentation.headerWithName("Authorization")
                                    .description("Basic auth credentials")),
                    PayloadDocumentation.responseFields(
                            PayloadDocumentation.fieldWithPath("access_token")
                                    .description("The access token string as issued by the "
                                            + "authorization server."),
                            PayloadDocumentation.fieldWithPath("token_type")
                                    .description("The type of token this is, typically just the "
                                            + "string \"bearer\"."),
                            PayloadDocumentation.fieldWithPath("refresh_token").optional()
                                    .description(" If the access token will expire, then it is "
                                            + "useful to return a refresh token which applications "
                                            + "can use to obtain another access token. However, "
                                            + "tokens issued with the implicit grant cannot be "
                                            + "issued a refresh token."),
                            PayloadDocumentation.fieldWithPath("expires_in").optional()
                                    .description("If the access token expires, the server should "
                                            + "reply with the duration of time the access token "
                                            + "is granted for."),
                            PayloadDocumentation.fieldWithPath("scope").optional()
                                    .description("If the scope the user granted is identical to "
                                            + "the scope the app requested, this parameter is "
                                            + "optional. If the granted scope is different from "
                                            + "the requested scope, such as if the user modified "
                                            + "the scope, then this parameter is required."),
                            PayloadDocumentation.fieldWithPath("jti")
                                    .description("(JWT ID) claim provides a unique identifier for "
                                            + "the JWT."))));
  }

  @Test
  void refreshAuthorizationExample() throws Exception {

    String jsonResponse = getAuthorizationResponse();

    JSONObject jsonObject = new JSONObject(jsonResponse);

    MultiValueMap<String, String> refreshParams = new LinkedMultiValueMap<>();
    refreshParams.add("grant_type", "refresh_token");
    refreshParams.add("refresh_token", (String) jsonObject.get("refresh_token"));

    this.mockMvc.perform(
            MockMvcRequestBuilders.post("/oauth/token")
                    .params(refreshParams)
                    .with(SecurityMockMvcRequestPostProcessors.httpBasic(
                            "application-client", "password"))
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(MockMvcRestDocumentation.document("refresh-example",
                    RequestDocumentation.requestParameters(
                            RequestDocumentation.parameterWithName("grant_type")
                                    .description("This tells the server we’re using the Refresh "
                                            + "token grant type."),
                            RequestDocumentation.parameterWithName("refresh_token")
                                    .description("The refresh token previously issued to "
                                            + "the client.")),
                    HeaderDocumentation.requestHeaders(
                            HeaderDocumentation.headerWithName("Authorization")
                                    .description("Basic auth credentials")),
                    PayloadDocumentation.responseFields(
                            PayloadDocumentation.fieldWithPath("access_token")
                                    .description("The access token string as issued by the "
                                            + "authorization server."),
                            PayloadDocumentation.fieldWithPath("token_type")
                                    .description("The type of token this is, typically just the "
                                            + "string \"bearer\"."),
                            PayloadDocumentation.fieldWithPath("refresh_token").optional()
                                    .description(" If the access token will expire, then it is "
                                            + "useful to return a refresh token which applications "
                                            + "can use to obtain another access token. However, "
                                            + "tokens issued with the implicit grant cannot be "
                                            + "issued a refresh token."),
                            PayloadDocumentation.fieldWithPath("expires_in").optional()
                                    .description("If the access token expires, the server should "
                                            + "reply with the duration of time the access token "
                                            + "is granted for."),
                            PayloadDocumentation.fieldWithPath("scope").optional()
                                    .description("If the scope the user granted is identical to "
                                            + "the scope the app requested, this parameter is "
                                            + "optional. If the granted scope is different from "
                                            + "the requested scope, such as if the user modified "
                                            + "the scope, then this parameter is required."),
                            PayloadDocumentation.fieldWithPath("jti")
                                    .description("(JWT ID) claim provides a unique identifier for "
                                            + "the JWT."))));
  }


}
