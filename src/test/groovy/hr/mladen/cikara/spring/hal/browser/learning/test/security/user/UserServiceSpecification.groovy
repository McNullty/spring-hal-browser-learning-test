package hr.mladen.cikara.spring.hal.browser.learning.test.security.user

import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.test.context.junit.jupiter.SpringExtension
import spock.lang.Specification

@ExtendWith(SpringExtension.class)
class UserServiceSpecification extends Specification {

    private UserService userService

    private UserRepository userRepository = Mockito.mock(UserRepository)

    def setup() {
        userService = new UserServiceImpl(userRepository)
    }

    /**
     * This is excessive test because service is only calling repository so there is nothing to test
     */
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
        def test = new UserServiceImpl(null)

        then: 'assertion exception is thrown'
        thrown IllegalArgumentException
    }

    def 'findByUsername with existing username will return UserDetails'() {
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
        def result = userService.findByUsername("adam")

        then: 'result is not null'
        thrown UserService.UserNotFoundException
    }
}
