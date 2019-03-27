package hr.mladen.cikara.spring.hal.browser.learning.test.security;

import hr.mladen.cikara.spring.hal.browser.learning.test.IndexController;
import hr.mladen.cikara.spring.hal.browser.learning.test.util.SpringSecurityWebAuxTestConfig;
import lombok.AccessLevel;
import lombok.Getter;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * This specification is duplicated in groovy.
 */
@Getter(AccessLevel.PRIVATE)
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = IndexController.class)
@Import(SpringSecurityWebAuxTestConfig.class)
class IndexControllerJavaUnitSpecification {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void testIndexController() throws Exception {
    mockMvc.perform(
        MockMvcRequestBuilders.get("/")
            .accept(MediaTypes.HAL_JSON_VALUE))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath(
            "$._links.fx:resources-books.href", Matchers.is("http://localhost/books")))
        .andExpect(MockMvcResultMatchers.jsonPath(
            "$._links.fx:resources-users.href", Matchers.is("http://localhost/users")))
        .andExpect(MockMvcResultMatchers.jsonPath(
            "$._links.fx:authorization.href", Matchers.is("http://localhost/users/oauth/token")))
        .andExpect(MockMvcResultMatchers.jsonPath(
            "$._links.fx:register.href", Matchers.is("http://localhost/register")))
        .andExpect(MockMvcResultMatchers.jsonPath(
            "$._links.api-guide.href", Matchers.is("http://localhost/docs/api-guide.html")))
        .andExpect(MockMvcResultMatchers.jsonPath(
            "$._links.user-guide.href", Matchers.is("http://localhost/docs/user-guide.html")))
    ;
  }
}
