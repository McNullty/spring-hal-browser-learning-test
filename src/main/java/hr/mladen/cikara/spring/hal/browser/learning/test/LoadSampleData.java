package hr.mladen.cikara.spring.hal.browser.learning.test;

import hr.mladen.cikara.spring.hal.browser.learning.test.book.Book;
import hr.mladen.cikara.spring.hal.browser.learning.test.book.BookRepository;
import hr.mladen.cikara.spring.hal.browser.learning.test.security.user.User;
import hr.mladen.cikara.spring.hal.browser.learning.test.security.user.UserDao;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LoadSampleData implements ApplicationListener<ContextRefreshedEvent> {

  private final BookRepository bookRepository;
  private final UserDao userDao;

  private final Random rand = new Random();

  public LoadSampleData(final BookRepository bookRepository,
                        final UserDao userDao) {
    this.bookRepository = bookRepository;
    this.userDao = userDao;
  }

  @Override
  public void onApplicationEvent(final ContextRefreshedEvent event) {
    log.info("Creating sample data");
    for (int i = 0; i < 100; i++) {

      Book book = Book.builder()
              .author("Test author " + i)
              .title("Test title " + i)
              .blurb("Test blurb " + i)
              .pages(rand.nextInt(500))
              .build();

      bookRepository.save(book);
    }

    final PasswordEncoder passwordEncoder =
            PasswordEncoderFactories.createDelegatingPasswordEncoder();

    User alexa = User.builder()
            .username("Alex123")
            .password(passwordEncoder.encode("password"))
            .build();

    userDao.save(alexa);

    User tom = User.builder()
            .username("Tom234")
            .password(passwordEncoder.encode("password"))
            .build();

    userDao.save(tom);

    User adam = User.builder()
            .username("Adam")
            .password(passwordEncoder.encode("password"))
            .build();


    userDao.save(adam);
  }
}
