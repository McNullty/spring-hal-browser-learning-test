package hr.mladen.cikara.spring.hal.browser.learning.test;

import hr.mladen.cikara.spring.hal.browser.learning.test.book.BooksController;
import hr.mladen.cikara.spring.hal.browser.learning.test.security.register.RegisterController;
import hr.mladen.cikara.spring.hal.browser.learning.test.security.register.RegisterDto;
import hr.mladen.cikara.spring.hal.browser.learning.test.security.user.UserController;
import java.util.Collections;
import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/")
public class IndexController {

  private static final String HREF = "href";
  private static final String TITLE = "title";

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

    Link bookLink = ControllerLinkBuilder.linkTo(BooksController.class).withSelfRel()
            .withTitle("Link to books resources");
    Link userLink = ControllerLinkBuilder.linkTo(UserController.class).withSelfRel()
            .withTitle("Link to users resources");
    Link indexLink = ControllerLinkBuilder.linkTo(IndexController.class).withSelfRel()
            .withTitle("API index page");

    Link registerLink = null;
    try {
      registerLink = ControllerLinkBuilder.linkTo(
          ControllerLinkBuilder.methodOn(RegisterController.class).register(RegisterDto.builder().build())).withSelfRel()
          .withTitle("Link for registering new users");
    } catch (Exception e) {
      log.warn("Building link for register controller thrown exception");
    }


    return factory.createObjectBuilder()
            .add("curies", factory.createArrayBuilder()
                    .add(factory.createObjectBuilder()
                            .add("name", "fx")
                            .add(HREF, indexLink.getHref() + "/docs/api-guide.html#{rel}")
                            .add("templated", Boolean.TRUE)
                    )
            )
            .add("fx:resources-books", factory.createObjectBuilder()
                    .add(HREF, bookLink.getHref())
                    .add(TITLE, bookLink.getTitle())
            )
            .add("fx:resources-users", factory.createObjectBuilder()
                    .add(HREF, userLink.getHref())
                    .add(TITLE, userLink.getTitle())
            )
            .add("fx:authorization", factory.createObjectBuilder()
                    .add(HREF, userLink.getHref() + "/oauth/token")
                    .add(TITLE, "OAuth2 endpoint for obtaining authorization tokens")
            )
            .add("fx:register", factory.createObjectBuilder()
                    .add(HREF, registerLink != null ? registerLink.getHref() : "")
                    .add(TITLE, registerLink != null ? registerLink.getTitle() : "")
            )
            .add("api-guide", factory.createObjectBuilder()
                    .add(HREF, indexLink.getHref() + "/docs/api-guide.html")
            )
            .add("user-guide", factory.createObjectBuilder()
                    .add(HREF, indexLink.getHref() + "/docs/user-guide.html")
            )
            .build();
  }
}
