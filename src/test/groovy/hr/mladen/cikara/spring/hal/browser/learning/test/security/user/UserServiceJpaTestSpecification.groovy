package hr.mladen.cikara.spring.hal.browser.learning.test.security.user

import hr.mladen.cikara.spring.hal.browser.learning.test.security.register.RegisterDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import spock.lang.Specification

@DataJpaTest
class UserServiceJpaTestSpecification extends Specification {

    @Autowired
    private TestEntityManager entityManager

    private UserService userService

    @Autowired
    private UserRepository userRepository

    def adamsName = "Adam"

    def setup() {
        userService = new UserServiceImpl(userRepository)

        // Setup mock registry
        final User adam = User.builder()
                .username(adamsName)
                .password("adamsPassword")
                .build()

        final User bob = User.builder()
                .username("bob")
                .password("bobsPassword")
                .build()

        entityManager.persist(adam)
        entityManager.persist(bob)
        entityManager.flush()
    }

    def 'changing password'() {
        given: 'valid changePasswordsDto'
        def changePasswordDto = ChangePasswordDto.builder()
                .password("newPassword")
                .passwordRepeated("newPassword")
                .build()

        when: 'changePassword service is called'
        userService.changePassword("Adam", changePasswordDto)

        then: 'password is changed'
        def user = userRepository.findByUsername(adamsName)
        user.get().getPassword() != "adamsPassword"
    }

    def 'when findAll is called page with two user is returned'() {
        when: 'findAll service is called with Pageable object is passed'
        Pageable pageable = new PageRequest(0,10)
        def result = userService.findAll(pageable)

        then: 'result is page with two entities'
        result.content.size() == 2
    }

    def 'when instantiating UserService without registry exception is thrown'() {
        when: 'new UserService with null registry is created'
        //noinspection GroovyResultOfObjectAllocationIgnored
        new UserServiceImpl(null)

        then: 'assertion exception is thrown'
        thrown IllegalArgumentException
    }

    def 'findByUsername with existing username will return User'() {
        when: 'loadUserByUsername service is called'
        def result = userService.findByUsername(adamsName)

        then: 'result is not null'
        result != null
    }

    def 'findByUsername with non existing username will return exception'() {
        when: 'loadUserByUsername service is called'
        userService.findByUsername("test")

        then: 'result is not null'
        thrown UserService.UserNotFoundException
    }

    def 'loadUserByUsername with existing username will return UserDetails'() {
        when: 'loadUserByUsername service is called'
        def result = ((UserDetailsService)userService).loadUserByUsername(adamsName)

        then: 'result is not null'
        result != null
    }

    def 'loadUserByUsername with non existing username will return exception'() {
        when: 'loadUserByUsername service is called'
        ((UserDetailsService)userService).loadUserByUsername("test")

        then: 'result is not null'
        thrown UsernameNotFoundException
    }

    def 'registering user'() {
        given: 'valid registerDto'
        def registerDto = RegisterDto.builder()
                .username("username")
                .password("password")
                .passwordRepeated("password")
                .build()

        when: 'register service is called'
        def result = userService.register(registerDto)

        then: 'new user is returned'
        result != null

        and: 'password is encrypted'
        result.password != "password"
    }

    def 'registering user mismatching passwords'() {
        given: 'invalid registerDto with mismatching passwords'
        def registerDto = RegisterDto.builder()
                .username("username")
                .password("password")
                .passwordRepeated("passowrd")
                .build()

        when: 'register service is called'
        userService.register(registerDto)

        then: 'Exception is thrown'
        thrown UserService.PasswordsDontMatch
    }

    def 'registering user with same username'() {
        given: 'valid registerDto'
        def registerDto = RegisterDto.builder()
                .username(adamsName)
                .password("password")
                .passwordRepeated("password")
                .build()

        when: 'register service is called'
        userService.register(registerDto)

        then: 'Exception is thrown'
        thrown UserService.UsernameAlreadyTakenException
    }
}
