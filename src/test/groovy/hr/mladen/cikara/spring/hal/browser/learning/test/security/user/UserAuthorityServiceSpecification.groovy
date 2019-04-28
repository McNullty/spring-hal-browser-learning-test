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
    private UserAuthorityRepository userAuthorityRepository

    private UserAuthorityService userAuthorityService

    def setup() {
        userAuthorityService = new UserAuthorityServiceImpl(userAuthorityRepository)
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
}
