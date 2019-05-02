package hr.mladen.cikara.spring.hal.browser.learning.test.security.user

import groovy.util.logging.Slf4j
import hr.mladen.cikara.spring.hal.browser.learning.test.AuthorizationUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.hateoas.MediaTypes
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
}
