package hr.mladen.cikara.spring.hal.browser.learning.test;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class IndexController {
  @RequestMapping(method = RequestMethod.GET, produces = {"application/hal+json"})
  ResponseEntity<?> index() {
    return ResponseEntity.ok(null);
  }
}
