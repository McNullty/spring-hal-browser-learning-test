package hr.mladen.cikara.spring.hal.browser.learning.test.security.user;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service(value = "userService")
public class UserServiceImpl implements UserDetailsService, UserService {

  private final UserRepository userRepository;

  @Autowired
  public UserServiceImpl(final UserRepository userRepository) {
    Assert.notNull(userRepository, "Service can't work without repository");

    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username);
    if(user == null){
      throw new UsernameNotFoundException("Invalid username or password.");
    }
    return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), getAuthority());
  }

  private List<SimpleGrantedAuthority> getAuthority() {
    return Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
  }

  public Page<User> findAll(Pageable pageable) {
    return userRepository.findAll(pageable);
  }

  @Override
  public void delete(long id) {
    userRepository.deleteById(id);
  }

  @Override
  public User findById(final Long userId) throws UserNotFoundException {
    try {
      Optional<User> user = userRepository.findById(userId);
      if (user.isPresent()) {
        return user.get();
      } else {
        throw new UserNotFoundException(userId);
      }

    } catch (IllegalArgumentException e) {
      throw new UserNotFoundException(userId);
    }
  }

  @Override
  public User save(User user) {
    return userRepository.save(user);
  }
}
