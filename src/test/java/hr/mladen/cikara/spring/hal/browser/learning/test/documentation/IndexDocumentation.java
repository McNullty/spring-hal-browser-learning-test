package hr.mladen.cikara.spring.hal.browser.learning.test.documentation;

import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class IndexDocumentation extends AbstractDocumentation {

  @Test
  @DisplayName("Documentation for index endpoint")
  public void indexExample() throws Exception {
    this.mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andDo(document("index-example",
                    links(
                            linkWithRel("books")
                                    .description("The <<resources-books,Books resource>>"),
                            linkWithRel("profile").description("The ALPS profile for the service")),
                    responseFields(
                            subsectionWithPath("_links")
                                    .description("<<resources-index-access_links,Links>> "
                                            + "to other resources"))));

  }
}
