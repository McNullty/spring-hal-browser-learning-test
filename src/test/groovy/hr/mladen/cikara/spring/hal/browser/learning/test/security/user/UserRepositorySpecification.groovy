package hr.mladen.cikara.spring.hal.browser.learning.test.security.user


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import spock.lang.Specification
/**
 * Unit test for UserRepository. It should test all methods that are specified by us and not added by Spring.
 */
@DataJpaTest
class UserRepositorySpecification extends Specification {

    @Autowired
    private TestEntityManager entityManager

    @Autowired
    private UserRepository userRepository

    def 'if there is user with given username findByUsername should find it'() {
        given: 'entity that is already stored in database'
        def AdamsName = "Adam"
        final User adam = User.builder()
                .username(AdamsName)
                .password("adamsPassword")
                .build()

        entityManager.persist(adam)
        entityManager.flush()

        when: 'repository is searched with findByUsername'
        final Optional<User> user = userRepository.findByUsername(AdamsName)

        then: 'user is found'
        user.isPresent()
    }

    def 'if there is no user with given username findByUsername should not find it'() {
        given: 'username that is not stored in database'
        def AdamsName = "Adam"

        when: 'repository is searched with findByUsername'
        final Optional<User> user = userRepository.findByUsername(AdamsName)

        then: 'user is not found'
        !user.isPresent()
    }
}
