package hr.mladen.cikara.spring.hal.browser.learning.test.book;

import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

@Getter(AccessLevel.PRIVATE)
@Component
public class BookToBookResourceAssembler implements ResourceAssembler<Book, BookResource> {

  private final EntityLinks entityLinks;

  public BookToBookResourceAssembler(final EntityLinks entityLinks) {
    this.entityLinks = entityLinks;
  }

  @Override
  public BookResource toResource(final Book entity) {
    BookResource bookResource = new BookResource(entity);
    bookResource.add(entityLinks.linkForSingleResource(Book.class, entity.getId()).withSelfRel());

    return bookResource;
  }
}
