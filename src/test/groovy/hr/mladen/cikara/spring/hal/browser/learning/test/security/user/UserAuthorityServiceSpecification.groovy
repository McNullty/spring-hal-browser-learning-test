package hr.mladen.cikara.spring.hal.browser.learning.test.security.user


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import spock.lang.Specification

@DataJpaTest
class UserAuthorityServiceSpecification extends Specification {
    @Autowired
    private TestEntityManager entityManager

    @Autowired
    private UserRepository userRepository

    @Autowired
    private UserAuthorityRepository userAuthorityRepository

    private UserService userService

    def setup() {
        userService = new UserServiceImpl(userRepository, userAuthorityRepository)
    }

    def 'service returns roles for user'() {
        given: 'repository has user with user authority'
        def userRole = UserAuthority.builder()
                .authority(UserAuthorityEnum.ROLE_USER.name()).build()
        def savedUserRole = entityManager.persist(userRole)

        def adamsName = "adam@first.com"
        final User adam = User.builder()
                .username(adamsName)
                .password("adamsPassword")
                .firstName("Adam")
                .lastName("First")
                .addAuthority(savedUserRole)
                .build()

        def savedUser = entityManager.persist(adam)
        entityManager.flush()

        when: 'service find method is called'
        def userAuthorities =
                userService.findAllAuthoritiesForUserId(savedUser.getId())

        then: 'collection of authorities is returned'
        userAuthorities.contains(savedUserRole)
    }

    def 'if non-exiting userId is sent exception is raised'() {
        given: 'user service is mocked to throw exception'

        when: 'service find method is called'
        userService.findAllAuthoritiesForUserId(1L)

        then: 'exception is raised'
        thrown UserService.UserNotFoundException
    }

    def 'deleting user authority from user'() {
        given: 'repository has user with user authority'
        def userRole = UserAuthority.builder()
                .authority(UserAuthorityEnum.ROLE_USER.name()).build()
        def savedUserRole = entityManager.persist(userRole)

        def adamsName = "adam@first.com"
        final User adam = User.builder()
                .username(adamsName)
                .password("adamsPassword")
                .firstName("Adam")
                .lastName("First")
                .addAuthority(savedUserRole)
                .build()

        def savedUser = entityManager.persist(adam)
        entityManager.flush()

        when: 'deleteAuthority method is called'
        userService.deleteAuthority(
                savedUser.getId(), UserAuthorityEnum.ROLE_USER.name())

        then: 'user authority is removed'
        def userAuthorities =
                userService.findAllAuthoritiesForUserId(savedUser.getId())
        userAuthorities.isEmpty()
    }
}
