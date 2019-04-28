package hr.mladen.cikara.spring.hal.browser.learning.test.security.user

import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.boot.test.mock.mockito.MockBean
import spock.lang.Specification

@DataJpaTest
class UserAuthorityServiceSpecification extends Specification {
    @Autowired
    private TestEntityManager entityManager

    @Autowired
    private UserAuthorityRepository userAuthorityRepository

    @MockBean
    private UserService userService

    private UserAuthorityService userAuthorityService

    def setup() {
        userAuthorityService = new UserAuthorityServiceImpl(userAuthorityRepository, userService)
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
                userAuthorityService.findAllAuthoritiesForUserId(savedUser.getId())

        then: 'collection of authorities is returned'
        userAuthorities.contains(savedUserRole)
    }

    def 'if non-exiting userId is sent exception is raised'() {
        given: 'user service is mocked to throw exception'
        Mockito.when(userService.findById(1L))
                .thenThrow(new UserService.UserNotFoundException(1L))

        when: 'service find method is called'
        userAuthorityService.findAllAuthoritiesForUserId(1L)

        then: 'exception is raised'
        thrown UserService.UserNotFoundException
    }
}
