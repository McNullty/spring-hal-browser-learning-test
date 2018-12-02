package hr.mladen.cikara.spring.hal.browser.learning.test.security.configuration;

import hr.mladen.cikara.spring.hal.browser.learning.test.error.handling.CustomEntryPoint;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

  @Override
  public void configure(HttpSecurity http) throws Exception {
    http
            .authorizeRequests()
            .antMatchers("/h2-console/**").permitAll()
            .antMatchers("/users/**").access("hasRole('ADMIN')")
            .antMatchers("/books/**").access("hasRole('ADMIN')")
            .and().exceptionHandling().authenticationEntryPoint(new CustomEntryPoint())
            .and()
            .csrf().disable()
            .headers().frameOptions().disable()
    ;
  }

}