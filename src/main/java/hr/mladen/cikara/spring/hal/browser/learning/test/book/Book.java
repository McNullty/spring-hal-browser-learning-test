package hr.mladen.cikara.spring.hal.browser.learning.test.book;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Entity
public class Book {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "book_id")
  private Long id;

  @NotNull
  @Column(columnDefinition = "VARCHAR", length = 100)
  private String title;

  @NotNull
  @Column(columnDefinition = "VARCHAR", length = 100)
  private String author;

  @Column(columnDefinition = "VARCHAR", length = 1000)
  private String blurb;

  private Integer pages;

  /**
   * For Hibernate.
   */
  Book() {
  }

  /**
   * For Hibernate.
   */
  Book(Long id, String title, String author, String blurb, Integer pages) {
    this.id = id;
    this.author = author;
    this.title = title;
    this.blurb = blurb;
    this.pages = pages;
  }
}
