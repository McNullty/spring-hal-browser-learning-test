package hr.mladen.cikara.spring.hal.browser.learning.test.links;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Link {
  private final String name;
  private final String href;
  private final Boolean templated;
}
