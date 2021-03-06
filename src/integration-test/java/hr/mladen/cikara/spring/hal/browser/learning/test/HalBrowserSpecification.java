package hr.mladen.cikara.spring.hal.browser.learning.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import hr.mladen.cikara.spring.hal.browser.learning.test.hal.browser.HalController;
import lombok.AccessLevel;
import lombok.Getter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@Getter(AccessLevel.PRIVATE)
@DisplayName("Given HalController")
@WebMvcTest(controllers = {HalController.class}, secure = false)
class HalBrowserSpecification {

  @Autowired
  private MockMvc mockMvc;

  @DisplayName("When accessing /browser/ endpoint should return HTTP 302 Found")
  @Test
  void testHalBrowserAccess() throws Exception {
    this.mockMvc
            .perform(get("/browser/"))
            .andDo(print())
            .andExpect(status().isFound());
  }

  @DisplayName("When accessing /browser/index.html endpoint should return HTTP 200 OK")
  @Test
  void testHalBrowserIndexAccess() throws Exception {
    this.mockMvc
            .perform(get("/browser/index.html"))
            .andDo(print())
            .andExpect(status().isOk());
  }
}
