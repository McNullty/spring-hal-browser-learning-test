package hr.mladen.cikara.spring.hal.browser.learning.test.security.user

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.json.JacksonJsonParser
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import spock.lang.Specification
import spock.lang.Unroll
/**
 * Integration test for UserController.
 */
@Slf4j
@Unroll
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerSpecification extends Specification {

    public static final String TEST_USER = "Alex123"
    @Autowired
    MockMvc mockMvc

    @Autowired
    UserRepository userRepository

    @Autowired
    private ObjectMapper objectMapper

    def 'find all users endpoint (users/)'() {
        when: 'you perform get operation'
        def result = mockMvc.perform(
                RestDocumentationRequestBuilders.get("/users/")
                        .header("Authorization", "Bearer " + getAuthorizationResponse())
                        .contentType(MediaType.APPLICATION_JSON))
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

    def 'changing password for self'() {
        given: 'valid authorization token'
        def authorization = getAuthorizationResponse()
        log.debug("Authorization: {}", authorization)

        and: 'test user from repository'
        def testUser =  userRepository.findByUsername(TEST_USER).get()

        and: 'user id for test user'
        def testUserId = testUser.getId()
        log.debug("User ID: {}", testUserId)

        and: 'old password is fetched from repository'
        def oldPassword = testUser.getPassword()
        log.debug("Old password: {}", oldPassword)

        and: 'request body with same password and repeated password'
        def requestBody = new HashMap<String, Object>()
        requestBody.put("password", "newPassword")
        requestBody.put("passwordRepeated", "newPassword")

        when: '/change-password endpoint is called'
        def result = mockMvc.perform(
                RestDocumentationRequestBuilders.put("/users/" + testUserId + "/change-password")
                        .header("Authorization", "Bearer " + getAuthorizationResponse())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andDo(MockMvcResultHandlers.print())

        then: 'NO_CONTENT is returned'
        result.andExpect(MockMvcResultMatchers.status().isNoContent())

        and: 'new password is saved in repository'
        def newTestUser =  userRepository.findByUsername(TEST_USER).get()
        log.debug("New password: {}", newTestUser.getPassword())

        newTestUser.getPassword() != oldPassword
    }

    def 'changing password for some other user'() {
        given: 'valid authorization token'
        def authorization = getAuthorizationResponse()
        log.debug("Authorization: {}", authorization)

        and: 'test user from repository'
        def testUser =  userRepository.findByUsername(TEST_USER).get()

        and: 'user id for some other user '
        def testUserId = testUser.getId() + 1
        log.debug("User ID: {}", testUserId)

        and: 'request body with same password and repeated password'
        def requestBody = new HashMap<String, Object>()
        requestBody.put("password", "newPassword")
        requestBody.put("passwordRepeated", "newPassword")

        when: '/change-password endpoint is called'
        def result = mockMvc.perform(
                RestDocumentationRequestBuilders.put("/users/" + testUserId + "/change-password")
                        .header("Authorization", "Bearer " + getAuthorizationResponse())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andDo(MockMvcResultHandlers.print())

        then: 'UNAUTHORIZED is returned'
        result.andExpect(MockMvcResultMatchers.status().isUnauthorized())
    }

    def 'changing password for nonexistent user'() {
        given: 'valid authorization token'
        def authorization = getAuthorizationResponse()
        log.debug("Authorization: {}", authorization)

        and: 'request body with same password and repeated password'
        def requestBody = new HashMap<String, Object>()
        requestBody.put("password", "newPassword")
        requestBody.put("passwordRepeated", "newPassword")

        when: '/change-password endpoint is called'
        def result = mockMvc.perform(
                RestDocumentationRequestBuilders.put("/users/" + 5000L + "/change-password")
                        .header("Authorization", "Bearer " + getAuthorizationResponse())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andDo(MockMvcResultHandlers.print())

        then: 'NOT_FOUND is returned'
        result.andExpect(MockMvcResultMatchers.status().isNotFound())
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
        loginParams.add("username", TEST_USER)
        loginParams.add("password", "password")

        String resultString = this.mockMvc.perform(
                MockMvcRequestBuilders.post("/oauth/token")
                        .params(loginParams)
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic(
                        "application-client", "password"))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString()

        JacksonJsonParser jsonParser = new JacksonJsonParser()
        return jsonParser.parseMap(resultString).get("access_token").toString()
    }
}
