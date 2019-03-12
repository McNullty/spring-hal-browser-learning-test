package hr.mladen.cikara.spring.hal.browser.learning.test.security.user

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.dao.DataIntegrityViolationException
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
        def adamsName = "Adam"
        final User adam = User.builder()
                .username(adamsName)
                .password("adamsPassword")
                .build()

        entityManager.persist(adam)
        entityManager.flush()

        when: 'repository is searched with findByUsername'
        final Optional<User> user = userRepository.findByUsername(adamsName)

        then: 'user is found'
        user.isPresent()
    }

    def 'if there is no user with given username findByUsername should not find it'() {
        given: 'username that is not stored in database'
        def adamsName = "Adam"

        when: 'repository is searched with findByUsername'
        final Optional<User> user = userRepository.findByUsername(adamsName)

        then: 'user is not found'
        !user.isPresent()
    }

    def 'if there is  no existing username in repository new user will be saved' () {
        given: 'that username is not already in repository'
        def adamsName = "Adam"
        final Optional<User> user = userRepository.findByUsername(adamsName)
        !user.isPresent()

        when: 'user with that username is saved in repository'
        final User adam = User.builder()
                .username(adamsName)
                .password("adamsPassword")
                .build()
        def storedUser = userRepository.save(adam)

        then: 'user is dtored in database'
        storedUser.id != null
    }

    def 'if there is already user with same username exception is thrown'() {
        given: 'entity that is already stored in database'
        def adamsName = "Adam"
        final User adam = User.builder()
                .username(adamsName)
                .password("adamsPassword")
                .build()

        entityManager.persist(adam)
        entityManager.flush()

        when: 'same username is persisted trough repository'
        final User secondAdam = User.builder()
                .username(adamsName)
                .password("secondAdamsPassword")
                .build()

        userRepository.saveAndFlush(secondAdam)


        then: 'exception is thrown'
        thrown DataIntegrityViolationException
    }
}
