package hr.mladen.cikara.spring.hal.browser.learning.test.util;

import java.util.Arrays;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * Test configuration for spring security.S
 */
@TestConfiguration
public class SpringSecurityWebAuxTestConfig {

  /**
   *
   * @return Mocked UserService
   */
  @Bean
  @Primary
  public UserDetailsService userService() {

    User basicUser = new User(
            "user@company.com", "password",  Arrays.asList(
            new SimpleGrantedAuthority("ROLE_USER"),
            new SimpleGrantedAuthority("ROLE_ADMIN")
    ));

    User managerUser = new User(
            "manager@company.com", "password",  Arrays.asList(
            new SimpleGrantedAuthority("ROLE_USER"),
            new SimpleGrantedAuthority("ROLE_ADMIN")
    ));

    return new InMemoryUserDetailsManager(Arrays.asList(
            basicUser, managerUser));
  }
}
