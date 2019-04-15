package hr.mladen.cikara.spring.hal.browser.learning.test.security.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<UserAuthority, Long> {
}
