package hr.mladen.cikara.spring.hal.browser.learning.test.security.user

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import spock.lang.Specification

@DataJpaTest
class UserAuthorityRepositorySpecification extends Specification {

    @Autowired
    private TestEntityManager entityManager

    @Autowired
    private UserAuthorityRepository userAuthorityRepository

    def 'if user has authorities repository should return them'() {

        given: 'ROLE_USER exists'
        def userRole = UserAuthority.builder()
                .authority(UserAuthorityEnum.ROLE_USER.name()).build()

        def savedUserRole = entityManager.persist(userRole)

        and: 'user with ROLE_USER authority'
        def adamsName = "adam@first.com"
        final User adam = User.builder()
                .username(adamsName)
                .password("adamsPassword")
                .firstName("Adam")
                .lastName("First")
                .addAuthority(userRole)
                .build()

        def savedUser = entityManager.persist(adam)
        entityManager.flush()

        when: 'findAllUserAuthorityByUserId method is call'
        def userAuthorities = userAuthorityRepository.findAllUserAuthorityByUserId(
                savedUser.getId())

        then: 'list with ROLE_USER is returned'
        userAuthorities.contains(savedUserRole)
    }

    def 'is user doesnt exist empty list is returned'() {
        when: 'findAllUserAuthorityByUserId method is call'
        def userAuthorities = userAuthorityRepository.findAllUserAuthorityByUserId(1L)

        then: 'empty list is returned'
        userAuthorities.isEmpty()
    }
}
