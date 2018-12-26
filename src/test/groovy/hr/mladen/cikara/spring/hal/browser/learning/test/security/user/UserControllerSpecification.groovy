package hr.mladen.cikara.spring.hal.browser.learning.test.security.user


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification
import spock.lang.Unroll
/**
 * Integration test for UserController.
 */
@Unroll
@SpringBootTest
class UserControllerSpecification extends Specification {

    MockMvc mockMvc

//    @Autowired
//    ObjectMapper objectMapper

    @Autowired
    WebApplicationContext webApplicationContext

//    @Autowired
//    UserRepository userRepository

    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build()
    }

    def 'find all users endpoint (users/)'() {
        when: 'you perform get operation'
        def result = mockMvc.perform(
                RestDocumentationRequestBuilders.get("/users/")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())

        then: 'expect to get status OK'
        result.andExpect(MockMvcResultMatchers.status().isOk())

        and: 'result should have json with this fields'
        //TODO: add tests
//        result.andExpect()
    }

    def 'getting data for current user'() {
        given: 'valid authorization token'
        when: '/me endpoint is access'
        then: 'OK is returned'
        and: 'user resource is returned'
    }

    /**
     * Sends authorization data and returns response from oauth2 server.
     *
     * @return Authorization response
     * @throws Exception mockMvc can return exception
     */
    String getAuthorizationResponse() throws Exception {
        MultiValueMap<String, String> loginParams = new LinkedMultiValueMap<>()
        loginParams.add("grant_type", "password")
        loginParams.add("username", "Alex123")
        loginParams.add("password", "password")

        return this.mockMvc.perform(
                MockMvcRequestBuilders.post("/oauth/token")
                        .params(loginParams)
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic(
                        "application-client", "password"))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString()
    }
}
