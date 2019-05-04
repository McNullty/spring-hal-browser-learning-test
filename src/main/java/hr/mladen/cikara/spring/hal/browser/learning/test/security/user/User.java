package hr.mladen.cikara.spring.hal.browser.learning.test.security.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Setter(value = AccessLevel.PACKAGE)
@Getter
@Builder
@Entity
@Table(name = "oauth2_user")
public class User implements UserDetails {

  public static final long serialVersionUID = 4238270444334620832L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Long id;

  @Column(unique = true)
  private String username;

  @Column
  @JsonIgnore
  private String password;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  @Setter(AccessLevel.NONE)
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
          name = "oauth2_user_authority",
          joinColumns = @JoinColumn(name = "user_id"),
          inverseJoinColumns = @JoinColumn(name = "authority_id")
  )
  @JsonIgnore
  private Set<UserAuthority> authorities;

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities.stream()
            .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
            .collect(Collectors.toSet());
  }

  /**
   * Returns a user authority if user has it, otherwise Optional.empty is returned.
   * @param userAuthority User authority to find
   * @return Optional User Authority
   */
  Optional<UserAuthority> getAuthority(final UserAuthorityEnum userAuthority) {
    return authorities.stream()
            .filter(userAuthority1 -> userAuthority1.getAuthority().equalsIgnoreCase(userAuthority.name()))
            .findFirst();
  }

  /**
   * Removes User Authority from entity
   *
   * @param userAuthority User Authority to remove
   * @return User entity without User Authority
   */
  User removeUserAuthority(final UserAuthority userAuthority) {
    authorities.remove(userAuthority);

    return this;
  }

  public static class UserBuilder {
    /**
     * Adding authority to list of authorities.
     *
     * @param userAuthority authority to add to list
     * @return builder
     */
    public UserBuilder addAuthority(final UserAuthority userAuthority) {
      if (this.authorities == null) {
        this.authorities = new HashSet<>();
      }

      this.authorities.add(userAuthority);

      return this;
    }
  }
}
