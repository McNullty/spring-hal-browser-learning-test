package hr.mladen.cikara.spring.hal.browser.learning.test

import hr.mladen.cikara.spring.hal.browser.learning.test.util.SpringSecurityWebAuxTestConfig
import org.hamcrest.Matchers
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.hateoas.MediaTypes
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = IndexController.class)
@Import(SpringSecurityWebAuxTestConfig.class)
class IndexControllerGroovyUnitSpecification extends Specification {

    @Autowired
    private MockMvc mockMvc

    def 'Index controller returns list of links to resources'() {
        when: 'preforming get operation'
        def result = mockMvc.perform(
                MockMvcRequestBuilders.get("/")
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andDo(MockMvcResultHandlers.print())

        then: 'OK is returned'
        result.andExpect(MockMvcResultMatchers.status().isOk())

        and: 'Link for book resource is returned'
        result.andExpect(MockMvcResultMatchers.jsonPath(
                '$._links.fx:resources-books.href', Matchers.is('http://localhost/books')))

        and: 'Link for user resource is returned'
        result.andExpect(MockMvcResultMatchers.jsonPath(
                '$._links.fx:resources-users.href', Matchers.is('http://localhost/users')))

        and: 'Link for authorization is returned'
        result.andExpect(MockMvcResultMatchers.jsonPath(
                '$._links.fx:authorization.href', Matchers.is('http://localhost/users/oauth/token')))

        and: 'Link for registering is returned'
        result.andExpect(MockMvcResultMatchers.jsonPath(
                '$._links.fx:register.href', Matchers.is('http://localhost/users/register')))

        and: 'Link for api guide is returned'
        result.andExpect(MockMvcResultMatchers.jsonPath(
                '$._links.api-guide.href', Matchers.is('http://localhost/docs/api-guide.html')))

        and: 'Link for user-guide is returned'
        result.andExpect(MockMvcResultMatchers.jsonPath(
                '$._links.user-guide.href', Matchers.is('http://localhost/docs/user-guide.html')))
    }
}
