package hr.mladen.cikara.spring.hal.browser.learning.test;

import hr.mladen.cikara.spring.hal.browser.learning.test.book.BooksController;
import hr.mladen.cikara.spring.hal.browser.learning.test.security.user.UserController;
import java.util.Collections;
import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class IndexController {

  @RequestMapping(method = RequestMethod.GET, produces = {MediaTypes.HAL_JSON_VALUE})
  ResponseEntity<?> index(final HttpServletRequest request) {

    JsonBuilderFactory factory = Json.createBuilderFactory(Collections.emptyMap());
    JsonObject object = factory.createObjectBuilder()
            .add("_links", createJsonObject(factory))
            .build();

    return ResponseEntity.ok(object.toString());
  }

  private JsonObject createJsonObject(
          final JsonBuilderFactory factory) {

    Link bookLink = ControllerLinkBuilder.linkTo(BooksController.class).withSelfRel();
    Link userLink = ControllerLinkBuilder.linkTo(UserController.class).withSelfRel();
    Link indexLink = ControllerLinkBuilder.linkTo(IndexController.class).withSelfRel();

    return factory.createObjectBuilder()
            .add("books", factory.createObjectBuilder()
                    .add("href", bookLink.getHref())
                    .add("templated", bookLink.isTemplated())
            )
            .add("users", factory.createObjectBuilder()
                    .add("href", userLink.getHref())
                    .add("templated", userLink.isTemplated()))
            .add("api-guide", factory.createObjectBuilder()
                    .add("href", indexLink.getHref() + "/docs/api-guide.html")
            )
            .add("user-guide", factory.createObjectBuilder()
                    .add("href", indexLink.getHref() + "/docs/user-guide.html")
            )
            .build();
  }
}
