package hr.mladen.cikara.spring.hal.browser.learning.test.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

  Page<Book> findByTitleContaining(String query, Pageable page);

  Page<Book> findByAuthorContaining(String query, Pageable page);
}
