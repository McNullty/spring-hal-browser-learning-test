package hr.mladen.cikara.spring.hal.browser.learning.test.security.register

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.util.logging.Slf4j
import hr.mladen.cikara.spring.hal.browser.learning.test.security.user.User
import hr.mladen.cikara.spring.hal.browser.learning.test.security.user.UserResource
import hr.mladen.cikara.spring.hal.browser.learning.test.security.user.UserService
import hr.mladen.cikara.spring.hal.browser.learning.test.security.user.UserToUserResourceAssembler
import hr.mladen.cikara.spring.hal.browser.learning.test.util.SpringSecurityWebAuxTestConfig
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.hateoas.Link
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import spock.lang.Specification

@Slf4j
@WebMvcTest(controllers = RegisterController.class)
@Import(SpringSecurityWebAuxTestConfig.class)
class RegisterControllerUnitSpecification extends Specification {

    @Autowired
    private MockMvc mockMvc

    @Autowired
    private ObjectMapper objectMapper

    @MockBean
    private UserService userService

    @MockBean
    private UserToUserResourceAssembler userToUserResourceAssembler

    def 'when registering new user with valid json created is returned'() {
        given: 'valid json'
        Map<String, Object> userJson = new HashMap<>()
        userJson.put("username", "adam")
        userJson.put("password", "testPassword")
        userJson.put("passwordRepeated", "testPassword")
        userJson.put("firstName", "Adam")
        userJson.put("lastName", "Last")

        and: 'User service is mocked to return registered user'
        User user = User.builder()
                .firstName("Adam")
                .lastName("Last")
                .username("adam")
                .password("testPassword")
                .build()

        Mockito.when(userService.register(Mockito.any(RegisterDto.class)))
                .thenReturn(user)

        and: 'UserToUserResourceAssembler is mocked to return UserResource'
        def mockedUserResource = new UserResource(user)
        Link selfLink = new Link("/users/1", "self")
        mockedUserResource.add(selfLink)

        Mockito.when(userToUserResourceAssembler.toResource(Mockito.any(User.class)))
                .thenReturn(mockedUserResource)


        when: 'register endpoint is called'
        def result = mockMvc.perform(
                RestDocumentationRequestBuilders.post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(userJson)))
                .andDo(MockMvcResultHandlers.print())

        then: 'created is returned'
        result.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().exists("Location"))
    }
}
