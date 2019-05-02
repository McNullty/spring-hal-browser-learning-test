package hr.mladen.cikara.spring.hal.browser.learning.test.security.configuration;

import hr.mladen.cikara.spring.hal.browser.learning.test.error.handling.CustomEntryPoint;
import hr.mladen.cikara.spring.hal.browser.learning.test.security.user.UserAuthorityEnum;

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
            .antMatchers("/users/register").permitAll()
            .antMatchers("/users/**/authorities")
              .hasAnyRole(
                      UserAuthorityEnum.ROLE_USER_MANAGER.getShortName(),
                      UserAuthorityEnum.ROLE_ADMIN.getShortName())
            .antMatchers("/users/**")
              .hasRole(UserAuthorityEnum.ROLE_USER.getShortName())
            .antMatchers("/books/**")
              .hasRole(UserAuthorityEnum.ROLE_USER.getShortName())
            .and().exceptionHandling().authenticationEntryPoint(new CustomEntryPoint())
            .and()
            .csrf().disable()
            .headers().frameOptions().disable()
    ;
  }

}