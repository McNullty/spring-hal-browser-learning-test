package hr.mladen.cikara.spring.hal.browser.learning.test;

import hr.mladen.cikara.spring.hal.browser.learning.test.book.Book;
import hr.mladen.cikara.spring.hal.browser.learning.test.book.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Random;

@Slf4j
@Component
public class LoadSampleData implements ApplicationListener<ContextRefreshedEvent> {

    private final BookRepository bookRepository;

    private Random rand = new Random();

    public LoadSampleData(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
      log.info("Creating sample data");
      for (int i=0; i<100; i++){
          Book book = new Book();
          book.setTitle("Test title " + i);
          book.setAuthor("Test author " + i);
          book.setBlurb("Test blurb " + i);
          book.setPages(rand.nextInt(500));

          bookRepository.save(book);
      }
    }
}
