package hr.mladen.cikara.spring.hal.browser.learning.test;

import hr.mladen.cikara.spring.hal.browser.learning.test.book.Book;
import hr.mladen.cikara.spring.hal.browser.learning.test.book.BookRepository;

import java.util.Random;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Profile("default")
@Getter(AccessLevel.PRIVATE)
@Component
public class LoadSampleDataForDefaultProfile implements ApplicationListener<ContextRefreshedEvent> {

  private final BookRepository bookRepository;

  private final Random rand = new Random();

  /**
   * Loading sample data for development.
   *
   * @param bookRepository Book Repository
   */
  public LoadSampleDataForDefaultProfile(
          final BookRepository bookRepository) {
    this.bookRepository = bookRepository;
  }

  @Override
  public void onApplicationEvent(final ContextRefreshedEvent event) {
    log.info("Creating sample data");
    insertBookSampleData();

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
