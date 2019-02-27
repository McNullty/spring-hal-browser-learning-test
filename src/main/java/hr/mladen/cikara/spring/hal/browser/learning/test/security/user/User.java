package hr.mladen.cikara.spring.hal.browser.learning.test.security.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "oauth2_user")
@Data
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Long id;

  @Column(unique = true)
  private String username;

  @Column
  @JsonIgnore
  private String password;

  /**
   * For Hibernate.
   */
  User() {
  }

  @Builder
  User(final String username, final String password) {
    this.username = username;
    this.password = password;
  }
}
