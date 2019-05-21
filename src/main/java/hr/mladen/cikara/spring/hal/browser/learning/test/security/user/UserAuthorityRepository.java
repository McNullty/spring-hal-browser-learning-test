package hr.mladen.cikara.spring.hal.browser.learning.test.security.user;

import java.util.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAuthorityRepository extends JpaRepository<UserAuthority, Long> {
  @Query("SELECT u.authorities FROM User u WHERE u.id = ?1")
  Collection<UserAuthority> findAllUserAuthorityByUserId(Long userId);
}
