package hr.mladen.cikara.spring.hal.browser.learning.test;

import hr.mladen.cikara.spring.hal.browser.learning.test.security.user.AuthorityRepository;
import hr.mladen.cikara.spring.hal.browser.learning.test.security.user.User;
import hr.mladen.cikara.spring.hal.browser.learning.test.security.user.UserAuthority;
import hr.mladen.cikara.spring.hal.browser.learning.test.security.user.UserAuthorityEnum;
import hr.mladen.cikara.spring.hal.browser.learning.test.security.user.UserRepository;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Profile({"dev", "default"})
@Getter(AccessLevel.PRIVATE)
@Component
public class LoadSampleDataForDevProfile implements ApplicationListener<ContextRefreshedEvent> {

  private final UserRepository userRepository;
  private final AuthorityRepository authorityRepository;

  public LoadSampleDataForDevProfile(
          final UserRepository userRepository,
          final AuthorityRepository authorityRepository) {
    this.userRepository = userRepository;
    this.authorityRepository = authorityRepository;
  }

  @Override
  public void onApplicationEvent(final ContextRefreshedEvent event) {
    log.info("Creating sample data");

    insertAuthorities();

    insertSampleUserData();
  }

  private void insertAuthorities() {
    authorityRepository.save(UserAuthority.builder()
            .authority(UserAuthorityEnum.ROLE_USER.name()).build());
    authorityRepository.save(UserAuthority.builder()
            .authority(UserAuthorityEnum.ROLE_ADMIN.name()).build());
    authorityRepository.save(UserAuthority.builder()
            .authority(UserAuthorityEnum.ROLE_USER_MANAGER.name()).build());
  }

  private void insertSampleUserData() {
    final PasswordEncoder passwordEncoder =
            PasswordEncoderFactories.createDelegatingPasswordEncoder();

    final UserAuthority roleUser =
            authorityRepository.findByAuthority(UserAuthorityEnum.ROLE_USER.name())
                    .orElse(null);
    final UserAuthority roleAdmin =
            authorityRepository.findByAuthority(UserAuthorityEnum.ROLE_ADMIN.name())
                    .orElse(null);
    final UserAuthority roleUserManager =
            authorityRepository.findByAuthority(UserAuthorityEnum.ROLE_USER_MANAGER.name())
                    .orElse(null);

    User alexa = User.builder()
            .username("Alex123")
            .password(passwordEncoder.encode("password"))
            .addAuthority(roleUser)
            .build();

    userRepository.save(alexa);

    User tom = User.builder()
            .username("Tom234")
            .password(passwordEncoder.encode("password"))
            .addAuthority(roleUser)
            .addAuthority(roleAdmin)
            .addAuthority(roleUserManager)
            .build();

    userRepository.save(tom);

    User adam = User.builder()
            .username("Adam")
            .password(passwordEncoder.encode("password"))
            .addAuthority(roleUser)
            .build();

    userRepository.save(adam);
  }
}
