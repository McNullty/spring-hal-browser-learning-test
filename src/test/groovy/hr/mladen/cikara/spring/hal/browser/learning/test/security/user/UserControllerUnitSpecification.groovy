package hr.mladen.cikara.spring.hal.browser.learning.test.security.user

import hr.mladen.cikara.spring.hal.browser.learning.test.util.SpringSecurityWebAuxTestConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

@WebMvcTest(controllers = UserController.class)
@Import(SpringSecurityWebAuxTestConfig.class)
class UserControllerUnitSpecification extends Specification{
    @Autowired
    private MockMvc mockMvc

    @MockBean
    private UserService userService

    @MockBean
    private UserToUserResourceAssembler userToUserResourceAssembler

    def setup() {

    }

    // TODO: finish test to show unit test for real controller
    def 'listing all users'() {
        given: ''
        when: ''
        then: ''
    }
}
