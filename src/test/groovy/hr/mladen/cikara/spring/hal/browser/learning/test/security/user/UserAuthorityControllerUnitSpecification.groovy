package hr.mladen.cikara.spring.hal.browser.learning.test.security.user

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.util.logging.Slf4j
import hr.mladen.cikara.spring.hal.browser.learning.test.util.SpringSecurityWebAuxTestConfig
import org.hamcrest.Matchers
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.hateoas.MediaTypes
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import spock.lang.Specification

@Slf4j
@WebMvcTest(controllers = UserAuthorityController.class)
@Import(SpringSecurityWebAuxTestConfig.class)
class UserAuthorityControllerUnitSpecification extends Specification {

    @Autowired
    private MockMvc mockMvc

    @Autowired
    private ObjectMapper objectMapper

    @MockBean
    private UserService userService

    def 'listing all roles for user'() {

        given: 'user service mocked for user with ID 1'
        Mockito.when(userService.findAllAuthoritiesForUserId(Mockito.any(Long.class)))
                .thenReturn(Arrays.asList(
                        new UserAuthority.UserAuthorityBuilder()
                                .id(1L).authority("ROLE_USER").build(),
                        new UserAuthority.UserAuthorityBuilder()
                                .id(2L).authority("ROLE_ADMIN").build()
                ))

        when: 'GET request to endpoint /users/1/authorities/'
        def result = mockMvc.perform(MockMvcRequestBuilders.get("/users/1/authorities/")
                .accept(MediaTypes.HAL_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())

        then: 'OK is returned'
        result.andExpect(MockMvcResultMatchers.status().isOk())

        and: 'result contains list of authorities'
        result.andExpect(
                MockMvcResultMatchers.jsonPath('$._embedded.authorities[*].authority',
                        Matchers.contains("ROLE_USER", "ROLE_ADMIN")))
    }

    def 'for non existing user error is returned'() {

        given: 'user service is mocked to return not found exception'
        Mockito.when(userService.findAllAuthoritiesForUserId(1L))
                .thenThrow(new UserService.UserNotFoundException(1L))

        when: 'GET request to endpoint /users/1/authorities/'
        def result = mockMvc.perform(MockMvcRequestBuilders.get("/users/1/authorities/")
                .accept(MediaTypes.HAL_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())

        then: 'result is 404 Not Found'
        result.andExpect(MockMvcResultMatchers.status().isNotFound())
    }

    def 'when deleting for user that doesnt exist not found is returned'() {
        given: 'user service is mocked to return not found exception'
        Mockito.when(userService.deleteAuthority(1L, "ROLE_USER_MANAGER"))
                .thenThrow(new UserService.UserNotFoundException(1L))

        when: 'DELETE request to endpoint /users/1/authorities/ROLE_USER_MANAGER'
        def result = mockMvc.perform(
                MockMvcRequestBuilders.delete("/users/1/authorities/ROLE_USER_MANAGER")
                .accept(MediaTypes.HAL_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())

        then: 'result is 404 Not Found'
        result.andExpect(MockMvcResultMatchers.status().isNotFound())
    }

    def 'when deleting for user authority that doesnt exist not found is returned'() {
        given: 'user service is mocked to return not found exception'
        Mockito.when(userService.deleteAuthority(1L, "ROLE_USER_MANAGER"))
                .thenThrow(new UserService.UserAuthorityNotFoundException(1L, "ROLE_USER_MANAGER"))

        when: 'DELETE request to endpoint /users/1/authorities/ROLE_USER_MANAGER'
        def result = mockMvc.perform(
                MockMvcRequestBuilders.delete("/users/1/authorities/ROLE_USER_MANAGER")
                .accept(MediaTypes.HAL_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())

        then: 'result is 404 Not Found'
        result.andExpect(MockMvcResultMatchers.status().isNotFound())
    }

    def 'when deleting valid user authority no content is returned'() {
        when: 'DELETE request to endpoint /users/1/authorities/ROLE_USER_MANAGER'
        def result = mockMvc.perform(
                MockMvcRequestBuilders.delete("/users/1/authorities/ROLE_USER_MANAGER")
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())

        then: 'result is No Content'
        result.andExpect(MockMvcResultMatchers.status().isNoContent())
    }

    def 'adding user roles to user'() {
        given: 'JSON with list of user authorities'
        def listAuthorities = Arrays.asList("ROLE_USER_MANAGER", "ROLE_ADMIN")

        def requestBody = new HashMap<>()
        requestBody.put("userAuthorities", listAuthorities)

        when: 'POST to /users/1/authorities'
        log.debug("Content: {}", objectMapper.writeValueAsString(requestBody))
        def result = mockMvc.perform(
                MockMvcRequestBuilders.post("/users/1/authorities")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andDo(MockMvcResultHandlers.print())

        then: 'OK is returned'
        result.andExpect(MockMvcResultMatchers.status().isCreated())

        and: 'link to user authorities is in Location header'
        result.andExpect(MockMvcResultMatchers.header().exists("Location"))
        result.andReturn().getResponse().getHeader("Location") == "http://localhost/users/1/authorities"
    }

    def 'sending empty list of user roles to add user authorities endpoint'() {
        given: 'JSON with empty list of user authorities'
        def listAuthorities = Collections.emptyList()

        def requestBody = new HashMap<>()
        requestBody.put("userAuthorities", listAuthorities)

        when: 'POST to /users/1/authorities'
        log.debug("Content: {}", objectMapper.writeValueAsString(requestBody))
        def result = mockMvc.perform(
                MockMvcRequestBuilders.post("/users/1/authorities")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andDo(MockMvcResultHandlers.print())

        then: 'BAD Request is returned'
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
    }

    def 'sending empty body to add user authorities endpoint'() {
        given: 'JSON with empty body'
        def requestBody = new HashMap<>()

        when: 'POST to /users/1/authorities'
        log.debug("Content: {}", objectMapper.writeValueAsString(requestBody))
        def result = mockMvc.perform(
                MockMvcRequestBuilders.post("/users/1/authorities")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andDo(MockMvcResultHandlers.print())

        then: 'BAD Request is returned'
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
    }

    def 'sending unknown user authority to add user authorities endpoint'() {
        given: 'JSON with list of user authorities'
        def listAuthorities = Arrays.asList("ROLE_USER_MANAGER", "ROLE_AMDIN")

        def requestBody = new HashMap<>()
        requestBody.put("userAuthorities", listAuthorities)

        when: 'POST to /users/1/authorities'
        log.debug("Content: {}", objectMapper.writeValueAsString(requestBody))
        def result = mockMvc.perform(
                MockMvcRequestBuilders.post("/users/1/authorities")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andDo(MockMvcResultHandlers.print())

        then: 'BAD Request is returned'
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
    }

}
