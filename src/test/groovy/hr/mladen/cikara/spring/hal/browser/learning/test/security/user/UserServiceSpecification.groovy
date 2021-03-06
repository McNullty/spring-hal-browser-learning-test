package hr.mladen.cikara.spring.hal.browser.learning.test.security.user

import hr.mladen.cikara.spring.hal.browser.learning.test.security.register.RegisterDto
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.test.context.junit.jupiter.SpringExtension
import spock.lang.Specification

@ExtendWith(SpringExtension.class)
class UserServiceSpecification extends Specification {

    private UserService userService

    private UserRepository userRepository = Mockito.mock(UserRepository)

    private UserAuthorityRepository userAuthorityRepository = Mockito.mock(UserAuthorityRepository)

    def setup() {
        userService = new UserServiceImpl(userRepository, userAuthorityRepository)
    }

    def 'when findAll is called page with two user is returned'() {
        given: 'repository with two users is mocked'
        final User adam = User.builder()
                .username("adam")
                .password("adamsPassword")
                .build()
        final User bob = User.builder()
                .username("bob")
                .password("bobsPassword")
                .build()
        List<User> userList = new ArrayList<>()
        userList.add(adam)
        userList.add(bob)
        Mockito.when(userRepository.findAll(Mockito.any(Pageable.class) as Pageable))
                .thenReturn(new PageImpl<>(userList))

        when: 'findAll service is called with Pageable object is passed'
        Pageable pageable = new PageRequest(0,10)
        def result = userService.findAll(pageable)

        then: 'result is page with two entities'
        result.content.size() == 2
    }

    def 'when instantiating UserService without registry exception is thrown'() {
        when: 'new UserService with null registry is created'
        //noinspection GroovyResultOfObjectAllocationIgnored
        new UserServiceImpl(null, userAuthorityRepository)

        then: 'assertion exception is thrown'
        thrown IllegalArgumentException
    }

    def 'findByUsername with existing username will return User'() {
        given: 'repository with user'
        final User adam = User.builder()
                .username("adam")
                .password("adamsPassword")
                .build()

        Mockito.when(userRepository.findByUsername(Mockito.anyString()))
                .thenReturn(Optional.of(adam))

        when: 'loadUserByUsername service is called'
        def result = userService.findByUsername("adam")

        then: 'result is not null'
        result != null
    }

    def 'findByUsername with non existing username will return exception'() {
        given: 'repository with no users'
        Mockito.when(userRepository.findByUsername(Mockito.anyString()))
                .thenReturn(Optional.empty())

        when: 'loadUserByUsername service is called'
        userService.findByUsername("adam")

        then: 'result is not null'
        thrown UserService.UserNotFoundException
    }

    def 'loadUserByUsername with existing username will return UserDetails'() {
        given: 'repository with user'
        final User adam = User.builder()
                .username("adam")
                .password("adamsPassword")
                .build()

        Mockito.when(userRepository.findByUsername(Mockito.anyString()))
                .thenReturn(Optional.of(adam))

        when: 'loadUserByUsername service is called'
        def result = ((UserDetailsService)userService).loadUserByUsername("adam")

        then: 'result is not null'
        result != null
    }

    def 'loadUserByUsername with non existing username will return exception'() {
        given: 'repository with no users'
        Mockito.when(userRepository.findByUsername(Mockito.anyString()))
                .thenReturn(Optional.empty())

        when: 'loadUserByUsername service is called'
        ((UserDetailsService)userService).loadUserByUsername("adam")

        then: 'result is not null'
        thrown UsernameNotFoundException
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
                .username("username")
                .password("password")
                .passwordRepeated("password")
                .build()

        and: 'user registry is mocked with user with same name'
        Mockito.when(userRepository.findByUsername(Mockito.anyString()))
                .thenReturn(Optional.of(User.builder()
                        .username("adam")
                        .password("adamsPassword")
                        .build()))

        when: 'register service is called'
        userService.register(registerDto)

        then: 'Exception is thrown'
        thrown UserService.UsernameAlreadyTakenException
    }
}
