package hr.mladen.cikara.spring.hal.browser.learning.test.security.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "custom.jwt")
@Data
public class JwtProperties {
  private String signingKey;
}
