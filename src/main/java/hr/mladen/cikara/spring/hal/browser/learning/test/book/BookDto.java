package hr.mladen.cikara.spring.hal.browser.learning.test.book;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class BookDto {
  private final String title;

  private final String author;

  private final String blurb;

  private final int pages;

  /**
   * DTO for mapping JSON data from request.
   *
   * @param author Authors name
   * @param title  Book title
   * @param blurb  Short description
   * @param pages  Number of pages
   */
  public BookDto(
          @JsonProperty("author") final String author,
          @JsonProperty("title") final String title,
          @JsonProperty("blurb") final String blurb,
          @JsonProperty("pages") final int pages) {
    this.title = title;
    this.author = author;
    this.blurb = blurb;
    this.pages = pages;
  }

  /**
   * Converting from DTO to Entity.
   *
   * @return Book entity
   */
  public Book getBook() {
    return Book.builder()
            .author(author)
            .title(title)
            .blurb(blurb)
            .pages(pages)
            .build();
  }
}
