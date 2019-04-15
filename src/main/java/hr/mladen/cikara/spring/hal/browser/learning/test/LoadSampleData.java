package hr.mladen.cikara.spring.hal.browser.learning.test;

import hr.mladen.cikara.spring.hal.browser.learning.test.book.Book;
import hr.mladen.cikara.spring.hal.browser.learning.test.book.BookRepository;
import hr.mladen.cikara.spring.hal.browser.learning.test.security.user.Authority;
import hr.mladen.cikara.spring.hal.browser.learning.test.security.user.AuthorityRepository;
import hr.mladen.cikara.spring.hal.browser.learning.test.security.user.User;
import hr.mladen.cikara.spring.hal.browser.learning.test.security.user.UserRepository;
import java.util.Random;
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
@Profile("default")
@Getter(AccessLevel.PRIVATE)
@Component
public class LoadSampleData implements ApplicationListener<ContextRefreshedEvent> {

  private final BookRepository bookRepository;
  private final UserRepository userRepository;
  private final AuthorityRepository authorityRepository;

  private final Random rand = new Random();

  public LoadSampleData(final BookRepository bookRepository,
                        final UserRepository userRepository,
                        final AuthorityRepository authorityRepository) {
    this.bookRepository = bookRepository;
    this.userRepository = userRepository;
    this.authorityRepository = authorityRepository;
  }

  @Override
  public void onApplicationEvent(final ContextRefreshedEvent event) {
    log.info("Creating sample data");
    insertBookSampleData();

    insertAuthorities();

    insertSampleUserData();
  }

  private void insertAuthorities() {
    authorityRepository.save(Authority.builder().authority("ROLE_USER").build());
    authorityRepository.save(Authority.builder().authority("ROLE_ADMIN").build());
    authorityRepository.save(Authority.builder().authority("ROLE_USER_MANAGER").build());
  }

  private void insertSampleUserData() {
    final PasswordEncoder passwordEncoder =
            PasswordEncoderFactories.createDelegatingPasswordEncoder();

    User alexa = User.builder()
            .username("Alex123")
            .password(passwordEncoder.encode("password"))
            .build();

    userRepository.save(alexa);

    User tom = User.builder()
            .username("Tom234")
            .password(passwordEncoder.encode("password"))
            .build();

    userRepository.save(tom);

    User adam = User.builder()
            .username("Adam")
            .password(passwordEncoder.encode("password"))
            .build();


    userRepository.save(adam);
  }

  private void insertBookSampleData() {
    for (int i = 0; i < 100; i++) {

      Book book = Book.builder()
              .author("Test author " + i)
              .title("Test title " + i)
              .blurb("Test blurb " + i)
              .pages(rand.nextInt(500))
              .build();

      bookRepository.save(book);
    }
  }
}
