package hr.mladen.cikara.spring.hal.browser.learning.test.security.user

import com.fasterxml.jackson.databind.ObjectMapper
import hr.mladen.cikara.spring.hal.browser.learning.test.security.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
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

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    WebApplicationContext webApplicationContext

    @Autowired
    UserRepository userRepository

    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
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
    }
}
