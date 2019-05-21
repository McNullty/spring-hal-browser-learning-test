package hr.mladen.cikara.spring.hal.browser.learning.test.security.user

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.util.logging.Slf4j
import hr.mladen.cikara.spring.hal.browser.learning.test.AuthorizationUtil
import org.hamcrest.Matchers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
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
    private MockMvc mockMvc

    @Autowired
    private UserRepository userRepository

    @Autowired
    private ObjectMapper objectMapper

    private AuthorizationUtil authorizationUtil

    def setup() {
        authorizationUtil = new AuthorizationUtil(this.mockMvc)
    }

    def 'Find all users endpoint (users/)'() {
        when: 'you perform get operation'
        def result = mockMvc.perform(
                MockMvcRequestBuilders.get("/users/")
                        .header("Authorization",
                        "Bearer " + authorizationUtil.getAccessTokenFromAuthorizationResponse(
                                TEST_USER, "password"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())

        then: 'expect to get status OK'
        result.andExpect(MockMvcResultMatchers.status().isOk())

        and: 'result should have json with this fields'
        result.andExpect(MockMvcResultMatchers.jsonPath(
                '$._embedded.users', Matchers.is(Matchers.notNullValue())))
    }

    def 'Getting data for current user'() {
        when: '/me endpoint is access'
        def result = mockMvc.perform(
                MockMvcRequestBuilders.get("/users/me")
                        .header("Authorization",
                        "Bearer " + authorizationUtil.getAccessTokenFromAuthorizationResponse(
                                TEST_USER, "password"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
        then: 'OK is returned'
        result.andExpect(MockMvcResultMatchers.status().isOk())
        and: 'user resource is returned'
        result.andExpect(MockMvcResultMatchers.jsonPath(
                'username', Matchers.is(TEST_USER)))
    }

    def 'Changing password'() {
        given: 'valid authorization token'
        def authorization = authorizationUtil.getAccessTokenFromAuthorizationResponse(
                TEST_USER, "password")
        log.debug("Authorization: {}", authorization)

        and: 'test user from repository'
        def testUser =  userRepository.findByUsername(TEST_USER).get()

        and: 'old password is fetched from repository'
        def oldPassword = testUser.getPassword()
        log.debug("Old password: {}", oldPassword)

        and: 'request body with same password and repeated password'
        def requestBody = new HashMap<String, Object>()
        requestBody.put("password", "password")
        requestBody.put("passwordRepeated", "password")

        when: '/change-password endpoint is called'
        def result = mockMvc.perform(
                MockMvcRequestBuilders.put("/users/me/change-password")
                        .header("Authorization", "Bearer " + authorization)
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

    def 'Changing password with invalid data'() {
        given: 'valid authorization token'
        def authorization = authorizationUtil.getAccessTokenFromAuthorizationResponse(
                TEST_USER, "password")
        log.debug("Authorization: {}", authorization)

        and: 'request body with same password and repeated password'
        def requestBody = new HashMap<String, Object>()
        requestBody.put("password", "password")
        requestBody.put("passwordRepeated", "differentPassword")

        when: '/change-password endpoint is called'
        def result = mockMvc.perform(
                MockMvcRequestBuilders.put("/users/me/change-password")
                        .header("Authorization", "Bearer " + authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andDo(MockMvcResultHandlers.print())

        then: 'NO_CONTENT is returned'
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())

        and: 'error message contains explanation'
        result.andExpect(MockMvcResultMatchers.jsonPath(
                '$.message', Matchers.is("Passwords dont match")))
    }
}
