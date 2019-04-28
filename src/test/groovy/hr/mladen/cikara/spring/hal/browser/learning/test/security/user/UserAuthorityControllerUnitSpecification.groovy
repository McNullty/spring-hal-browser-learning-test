package hr.mladen.cikara.spring.hal.browser.learning.test.security.user

import hr.mladen.cikara.spring.hal.browser.learning.test.util.SpringSecurityWebAuxTestConfig
import org.hamcrest.Matchers
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.hateoas.MediaTypes
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import spock.lang.Specification

@WebMvcTest(controllers = UserAuthorityController.class)
@Import(SpringSecurityWebAuxTestConfig.class)
class UserAuthorityControllerUnitSpecification extends Specification {

    @Autowired
    private MockMvc mockMvc

    @MockBean
    private UserAuthorityService userAuthorityService

    def 'listing all roles for user'() {

        given: 'user service mocked for user with ID 1'
        Mockito.when(userAuthorityService.findAllAuthoritiesForUserId(Mockito.any(Long.class)))
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
        Mockito.when(userAuthorityService.findAllAuthoritiesForUserId(1L))
                .thenThrow(new UserService.UserNotFoundException(1L))

        when: 'GET request to endpoint /users/1/authorities/'
        def result = mockMvc.perform(MockMvcRequestBuilders.get("/users/1/authorities/")
                .accept(MediaTypes.HAL_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())

        then: 'result is 404 Not Found'
        result.andExpect(MockMvcResultMatchers.status().isNotFound())
    }
}
