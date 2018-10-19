package hr.mladen.cikara.spring.hal.browser.learning.test.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends PagingAndSortingRepository<Book, Long> {

  Page<Book> findByTitleContaining(@Param("query") String query, Pageable page);

  Page<Book> findByAuthorContaining(@Param("query") String query, Pageable page);
}
