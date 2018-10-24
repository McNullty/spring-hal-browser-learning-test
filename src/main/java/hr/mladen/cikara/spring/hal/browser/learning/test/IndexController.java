package hr.mladen.cikara.spring.hal.browser.learning.test;

import hr.mladen.cikara.spring.hal.browser.learning.test.book.BooksController;
import hr.mladen.cikara.spring.hal.browser.learning.test.links.Link;
import hr.mladen.cikara.spring.hal.browser.learning.test.links.LinkExtractor;
import java.util.Collections;
import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class IndexController {

  public static org.springframework.hateoas.Link getSelfLink() {
    return ControllerLinkBuilder.linkTo(IndexController.class).withSelfRel();
  }

  @RequestMapping(method = RequestMethod.GET, produces = {RestMediaTypes.APPLICATION_HAL_JSON})
  ResponseEntity<?> index(final HttpServletRequest request) {

    JsonBuilderFactory factory = Json.createBuilderFactory(Collections.emptyMap());
    JsonObject object = factory.createObjectBuilder()
            .add("_links", createJsonObject(factory, request))
            .build();

    return ResponseEntity.ok(object.toString());
  }

  private JsonObject createJsonObject(
          final JsonBuilderFactory factory, final HttpServletRequest request) {

    LinkExtractor indexExtractor = new LinkExtractor(IndexController.class);
    Link indexLink = indexExtractor.getLink();

    LinkExtractor booksExtractor = new LinkExtractor(BooksController.class);
    Link booksLink = booksExtractor.getLink();
    return factory.createObjectBuilder()
                    .add(booksLink.getName(), factory.createObjectBuilder()
                            .add("href", booksLink.getHref())
                            .add("templated", booksLink.getTemplated())
                    )
                    .add("docs", factory.createObjectBuilder()
                            .add("href", indexLink.getHref() + "/docs/api-guide.html")
                    )
            .build();
  }
}
