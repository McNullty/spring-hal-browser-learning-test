package hr.mladen.cikara.spring.hal.browser.learning.test.book;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.core.Relation;

@Data
@EqualsAndHashCode(callSuper = false)
@Relation(value = "book", collectionRelation = "books")
class BookResource extends ResourceSupport {
  private final String title;

  private final String author;

  private final String blurb;

  private final int pages;

  BookResource(Book book) {
    this.title = book.getTitle();
    this.author = book.getAuthor();
    this.blurb = book.getBlurb();
    this.pages = book.getPages();
  }
}
