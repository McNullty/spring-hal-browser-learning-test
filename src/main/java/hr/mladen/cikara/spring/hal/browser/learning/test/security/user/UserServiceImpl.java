package hr.mladen.cikara.spring.hal.browser.learning.test.security.user;

import hr.mladen.cikara.spring.hal.browser.learning.test.security.register.RegisterDto;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Getter(AccessLevel.PRIVATE)
@Service(value = "userService")
public class UserServiceImpl implements UserDetailsService, UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder =
          PasswordEncoderFactories.createDelegatingPasswordEncoder();

  /**
   * Initializes UserService.
   *
   * @param userRepository UserRepository
   */
  @Autowired
  public UserServiceImpl(final UserRepository userRepository) {
    Assert.notNull(userRepository, "Service can't work without repository");

    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<User> user = userRepository.findByUsername(username);
    if (!user.isPresent()) {
      throw new UsernameNotFoundException("Invalid username or password.");
    }

    return new org.springframework.security.core.userdetails.User(
            user.get().getUsername(), user.get().getPassword(), getAuthority());
  }

  private List<SimpleGrantedAuthority> getAuthority() {
    return Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
  }

  @Override
  public Page<User> findAll(Pageable pageable) {
    return userRepository.findAll(pageable);
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
  public User register(final RegisterDto registerDto)
          throws UsernameAlreadyTakenException, PasswordsDontMatch {
    try {
      if (!registerDto.getPassword()
              .equals(registerDto.getPasswordRepeated())) {
        throw new PasswordsDontMatch();
      }

      User newUser = User.builder()
              .username(registerDto.getUsername())
              .password(passwordEncoder.encode(registerDto.getPassword()))
              .build();

      return userRepository.save(newUser);
    } catch (DataIntegrityViolationException e) {
      throw new UsernameAlreadyTakenException(registerDto.getUsername());
    }
  }

  @Override
  public User findByUsername(final String username) throws UserNotFoundException {
    try {
      Optional<User> user = userRepository.findByUsername(username);
      if (user.isPresent()) {
        return user.get();
      } else {
        throw new UserNotFoundException(username);
      }

    } catch (IllegalArgumentException e) {
      throw new UserNotFoundException(username);
    }
  }
}
