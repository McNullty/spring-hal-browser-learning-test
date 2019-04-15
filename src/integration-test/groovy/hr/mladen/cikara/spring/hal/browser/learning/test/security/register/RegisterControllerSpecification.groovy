package hr.mladen.cikara.spring.hal.browser.learning.test.security.register

import com.fasterxml.jackson.databind.ObjectMapper
import hr.mladen.cikara.spring.hal.browser.learning.test.security.user.User
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

@Unroll
@SpringBootTest
class RegisterControllerSpecification extends Specification {

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

    def 'registering new user with unique username should return Created'() {
        given: 'json with unique username'
        Map<String, Object> user = new HashMap<>()
        user.put("username", "adam")
        user.put("password", "testPassword")
        user.put("passwordRepeated", "testPassword")

        when: 'register endpoint is called'
        def result = mockMvc.perform(
                RestDocumentationRequestBuilders.post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(user)))
                .andDo(MockMvcResultHandlers.print())

        then: 'created http status is returned'
        result.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().exists("Location"))
    }

    def 'registering new user with non unique username should return '() {
        given: 'database with existing user'
        User adam = User.builder()
                .username("bob")
                .password("pass")
                .build()
        userRepository.saveAndFlush(adam)

        and: 'json with non unique username'
        Map<String, Object> user = new HashMap<>()
        user.put("username", "bob")
        user.put("password", "testPassword")
        user.put("passwordRepeated", "testPassword")

        when: 'register endpoint is called'
        def result = mockMvc.perform(
                RestDocumentationRequestBuilders.post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(user)))
                .andDo(MockMvcResultHandlers.print())

        then: 'bad request http status is returned'
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())

        and: 'message is "username already taken"'
        result.andExpect(MockMvcResultMatchers.jsonPath("message")
                .value("Username bob is already taken"))
    }

    def 'if user passwords dont match bad request is returned'() {
        given: 'json with passwords dont match'
        Map<String, Object> user = new HashMap<>()
        user.put("username", "charlie")
        user.put("password", "testPassword")
        user.put("passwordRepeated", "notMatching")

        when: 'register endpoint is called'
        def result = mockMvc.perform(
                RestDocumentationRequestBuilders.post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(user)))
                .andDo(MockMvcResultHandlers.print())

        then: 'bad request is called'
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())

        and: 'message is "passwords dont match"'
        result.andExpect(MockMvcResultMatchers.jsonPath("message")
                .value("Passwords dont match"))
    }
}
