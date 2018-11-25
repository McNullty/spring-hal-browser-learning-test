package hr.mladen.cikara.spring.hal.browser.learning.test.security.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Builder;
import lombok.Data;

@Entity
@Data
public class User {

  @Id
  @GeneratedValue(strategy= GenerationType.AUTO)
  private long id;

  @Column
  private String username;

  @Column
  @JsonIgnore
  private String password;

  @Column
  private long salary;

  @Column
  private int age;

  /**
   * For Hibernate
   */
  User() {
  }

  @Builder
  User(final String username, final String password) {
    this.username = username;
    this.password = password;
  }
}
