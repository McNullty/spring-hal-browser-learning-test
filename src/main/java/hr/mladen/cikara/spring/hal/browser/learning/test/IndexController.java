package hr.mladen.cikara.spring.hal.browser.learning.test;

import hr.mladen.cikara.spring.hal.browser.learning.test.book.BooksController;
import hr.mladen.cikara.spring.hal.browser.learning.test.links.Link;
import hr.mladen.cikara.spring.hal.browser.learning.test.links.LinkExtractor;
import java.util.Collections;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class IndexController {

  @RequestMapping(method = RequestMethod.GET, produces = {RestMediaTypes.APPLICATION_HAL_JSON})
  ResponseEntity<?> index() {

    JsonBuilderFactory factory = Json.createBuilderFactory(Collections.emptyMap());
    JsonObject object = factory.createObjectBuilder()
            .add("_links", createJsonArray(factory))
            .build();

    return ResponseEntity.ok(object.toString());
  }

  private JsonArray createJsonArray(JsonBuilderFactory factory) {
    LinkExtractor booksExtractor = new LinkExtractor(BooksController.class);
    Link booksLink = booksExtractor.getLink();
    return factory.createArrayBuilder()
            .add(factory.createObjectBuilder()
                    .add(booksLink.getName(), factory.createObjectBuilder()
                            .add("href", booksLink.getHref())
                            .add("templated", booksLink.getTemplated())
                    )
                    .add("docs", factory.createObjectBuilder()
                            .add("href", "")
                    )
            )
            .build();
  }
}
