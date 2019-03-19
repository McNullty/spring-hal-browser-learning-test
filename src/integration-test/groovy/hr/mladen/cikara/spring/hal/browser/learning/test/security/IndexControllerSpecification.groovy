package hr.mladen.cikara.spring.hal.browser.learning.test.security

import org.hamcrest.Matchers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.hateoas.MediaTypes
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@SpringBootTest
@AutoConfigureMockMvc
class IndexControllerSpecification extends Specification {

    @Autowired
    MockMvc mockMvc

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
                '$._links.fx:register.href', Matchers.is('http://localhost/register')))

        and: 'Link for api guide is returned'
        result.andExpect(MockMvcResultMatchers.jsonPath(
                '$._links.api-guide.href', Matchers.is('http://localhost/docs/api-guide.html')))

        and: 'Link for user-guide is returned'
        result.andExpect(MockMvcResultMatchers.jsonPath(
                '$._links.user-guide.href', Matchers.is('http://localhost/docs/user-guide.html')))
    }
}
