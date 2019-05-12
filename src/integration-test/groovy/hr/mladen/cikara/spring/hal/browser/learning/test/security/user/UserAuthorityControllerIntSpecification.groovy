package hr.mladen.cikara.spring.hal.browser.learning.test.security.user

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.util.logging.Slf4j
import hr.mladen.cikara.spring.hal.browser.learning.test.AuthorizationUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.hateoas.MediaTypes
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import spock.lang.Specification
import spock.lang.Unroll

@Slf4j
@Unroll
@SpringBootTest
@AutoConfigureMockMvc
class UserAuthorityControllerIntSpecification extends Specification {

    @Autowired
    private MockMvc mockMvc

    @Autowired
    private ObjectMapper objectMapper

    @Autowired
    private UserRepository userRepository

    private AuthorizationUtil authorizationUtil

    def setup() {
        authorizationUtil = new AuthorizationUtil(this.mockMvc)
    }

    def 'Authenticated user must have USER_MANAGER or ADMIN'() {
        given: 'valid authorization token'
        def authorization = authorizationUtil.getAccessTokenFromAuthorizationResponse(
                "Alex123", "password")
        log.debug("Authorization: {}", authorization)

        when: 'GET request to endpoint /users/1/authorities/'
        def result = mockMvc.perform(MockMvcRequestBuilders.get("/users/1/authorities/")
                .header("Authorization", "Bearer " + authorization)
                .accept(MediaTypes.HAL_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())

        then: 'not authorized is returned'
        result.andExpect(MockMvcResultMatchers.status().isForbidden())
    }

    def 'User with USER_MANAGER role gets list od user authorities'() {
        given: 'valid authorization token with USER_MANAGER or ADMIN roles'
        def authorization = authorizationUtil.getAccessTokenFromAuthorizationResponse(
                "Tom234", "password")
        log.debug("Authorization: {}", authorization)

        when: 'GET request to endpoint /users/1/authorities/'
        def result = mockMvc.perform(MockMvcRequestBuilders.get("/users/1/authorities/")
                .header("Authorization", "Bearer " + authorization)
                .accept(MediaTypes.HAL_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())

        then: 'OK is returned'
        result.andExpect(MockMvcResultMatchers.status().isOk())
    }

    def 'when deleting for user that doesnt exist not found is returned'() {
        given: 'valid authorization token with USER_MANAGER or ADMIN roles'
        def authorization = authorizationUtil.getAccessTokenFromAuthorizationResponse(
                "Tom234", "password")

        when: 'DELETE request to endpoint /users/1/authorities/ROLE_USER_MANAGER'
        def result = mockMvc.perform(
                MockMvcRequestBuilders.delete("/users/599/authorities/ROLE_USER_MANAGER")
                        .header("Authorization", "Bearer " + authorization)
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())

        then: 'result is 404 Not Found'
        result.andExpect(MockMvcResultMatchers.status().isNotFound())
    }

    def 'when deleting for user authority that doesnt exist not found is returned'() {
        given: 'valid authorization token with USER_MANAGER or ADMIN roles'
        def authorization = authorizationUtil.getAccessTokenFromAuthorizationResponse(
                "Tom234", "password")

        when: 'DELETE request to endpoint /users/1/authorities/ROLE_USER_MANAGER'
        def result = mockMvc.perform(
                MockMvcRequestBuilders.delete("/users/1/authorities/ROLE_USER_MANAGER")
                        .header("Authorization", "Bearer " + authorization)
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())

        then: 'result is 404 Not Found'
        result.andExpect(MockMvcResultMatchers.status().isNotFound())
    }

    def 'when deleting valid user authority no content is returned'() {
        given: 'valid authorization token with USER_MANAGER or ADMIN roles'
        def authorization = authorizationUtil.getAccessTokenFromAuthorizationResponse(
                "Tom234", "password")

        when: 'DELETE request to endpoint /users/1/authorities/ROLE_USER'
        def result = mockMvc.perform(
                MockMvcRequestBuilders.delete("/users/1/authorities/ROLE_USER")
                        .header("Authorization", "Bearer " + authorization)
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())

        then: 'result is No Content'
        result.andExpect(MockMvcResultMatchers.status().isNoContent())

        and: 'user authority is removed'
        def user = userRepository.findById(1L)
        user.get().getAuthorities().isEmpty()
    }

    def 'adding user roles to user'() {
        given: 'valid authorization token with USER_MANAGER or ADMIN roles'
        def authorization = authorizationUtil.getAccessTokenFromAuthorizationResponse(
                "Tom234", "password")

        and: 'JSON with list of user authorities'
        def listAuthorities = Arrays.asList("ROLE_USER_MANAGER", "ROLE_ADMIN")

        def requestBody = new HashMap<>()
        requestBody.put("userAuthorities", listAuthorities)

        when: 'POST to /users/1/authorities'
        log.debug("Content: {}", objectMapper.writeValueAsString(requestBody))
        def result = mockMvc.perform(
                MockMvcRequestBuilders.post("/users/1/authorities")
                        .header("Authorization", "Bearer " + authorization)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andDo(MockMvcResultHandlers.print())

        then: 'OK is returned'
        result.andExpect(MockMvcResultMatchers.status().isCreated())

        and: 'link to user authorities is in Location header'
        result.andExpect(MockMvcResultMatchers.header().exists("Location"))
        result.andReturn().getResponse().getHeader("Location") == "http://localhost/users/1/authorities"

        and: 'user now has all three roles'
        def user = userRepository.findById(1L)
        user.get().getAuthorities().size() == 3
    }

    def 'sending empty list of user roles to add user authorities endpoint'() {
        given: 'valid authorization token with USER_MANAGER or ADMIN roles'
        def authorization = authorizationUtil.getAccessTokenFromAuthorizationResponse(
                "Tom234", "password")

        and: 'JSON with empty list of user authorities'
        def listAuthorities = Collections.emptyList()

        def requestBody = new HashMap<>()
        requestBody.put("userAuthorities", listAuthorities)

        when: 'POST to /users/1/authorities'
        log.debug("Content: {}", objectMapper.writeValueAsString(requestBody))
        def result = mockMvc.perform(
                MockMvcRequestBuilders.post("/users/1/authorities")
                        .header("Authorization", "Bearer " + authorization)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andDo(MockMvcResultHandlers.print())

        then: 'BAD Request is returned'
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
    }

    def 'sending empty body to add user authorities endpoint'() {
        given: 'valid authorization token with USER_MANAGER or ADMIN roles'
        def authorization = authorizationUtil.getAccessTokenFromAuthorizationResponse(
                "Tom234", "password")

        and: 'JSON with empty body'
        def requestBody = new HashMap<>()

        when: 'POST to /users/1/authorities'
        log.debug("Content: {}", objectMapper.writeValueAsString(requestBody))
        def result = mockMvc.perform(
                MockMvcRequestBuilders.post("/users/1/authorities")
                        .header("Authorization", "Bearer " + authorization)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andDo(MockMvcResultHandlers.print())

        then: 'BAD Request is returned'
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
    }

    def 'sending unknown user authority to add user authorities endpoint'() {
        given: 'valid authorization token with USER_MANAGER or ADMIN roles'
        def authorization = authorizationUtil.getAccessTokenFromAuthorizationResponse(
                "Tom234", "password")

        and: 'JSON with list of user authorities'
        def listAuthorities = Arrays.asList("ROLE_USER_MANAGER", "ROLE_AMDIN")

        def requestBody = new HashMap<>()
        requestBody.put("userAuthorities", listAuthorities)

        when: 'POST to /users/1/authorities'
        log.debug("Content: {}", objectMapper.writeValueAsString(requestBody))
        def result = mockMvc.perform(
                MockMvcRequestBuilders.post("/users/1/authorities")
                        .header("Authorization", "Bearer " + authorization)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andDo(MockMvcResultHandlers.print())

        then: 'BAD Request is returned'
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
    }
}
