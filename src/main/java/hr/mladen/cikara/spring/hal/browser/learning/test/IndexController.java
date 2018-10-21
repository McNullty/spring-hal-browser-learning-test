package hr.mladen.cikara.spring.hal.browser.learning.test;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class IndexController {

  @RequestMapping(method = RequestMethod.GET, produces = {RestMediaTypes.APPLICATION_HAL_JSON})
  ResponseEntity<?> index() {
    return ResponseEntity.ok(null);
  }
}
