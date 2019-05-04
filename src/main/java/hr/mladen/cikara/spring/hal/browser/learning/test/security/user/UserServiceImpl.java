package hr.mladen.cikara.spring.hal.browser.learning.test.security.user;

import hr.mladen.cikara.spring.hal.browser.learning.test.security.register.RegisterDto;

import java.util.Collection;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Slf4j
@Getter(AccessLevel.PRIVATE)
@Service(value = "userService")
public class UserServiceImpl implements UserDetailsService, UserService {

  private final UserRepository userRepository;
  private final UserAuthorityRepository userAuthorityRepository;
  private final PasswordEncoder passwordEncoder =
      PasswordEncoderFactories.createDelegatingPasswordEncoder();

  /**
   * Initializes UserService.
   *
   * @param userRepository UserRepository
   * @param userAuthorityRepository UserAuthorityRepository
   */
  public UserServiceImpl(final UserRepository userRepository,
                         final UserAuthorityRepository userAuthorityRepository) {
    Assert.notNull(userRepository, "Service can't work without user repository");
    Assert.notNull(userAuthorityRepository,
            "Service can't work without user authority repository");

    this.userRepository = userRepository;
    this.userAuthorityRepository = userAuthorityRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<User> user = userRepository.findByUsername(username);
    if (!user.isPresent()) {
      throw new UsernameNotFoundException("Invalid username or password.");
    }

    return user.get();
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

    if (!registerDto.getPassword()
        .equals(registerDto.getPasswordRepeated())) {
      throw new PasswordsDontMatch();
    }

    Optional<User> existingUserWithSameUsername =
        userRepository.findByUsername(registerDto.getUsername());
    if (existingUserWithSameUsername.isPresent()) {
      throw new UsernameAlreadyTakenException(registerDto.getUsername());
    }

    User newUser = User.builder()
        .username(registerDto.getUsername())
        .password(passwordEncoder.encode(registerDto.getPassword()))
        .build();

    return userRepository.save(newUser);
  }

  @Override
  public User findByUsername(final String username) throws UserNotFoundException {
    Optional<User> user = userRepository.findByUsername(username);
    if (user.isPresent()) {
      return user.get();
    } else {
      throw new UserNotFoundException(username);
    }
  }

  @Override
  public void changePassword(
          String username, ChangePasswordDto changePasswordDto)
          throws PasswordsDontMatch, UserNotFoundException {

    if (!changePasswordDto.getPassword()
            .equals(changePasswordDto.getPasswordRepeated())) {
      throw new PasswordsDontMatch();
    }

    Optional<User> user = userRepository.findByUsername(username);

    if (!user.isPresent()) {
      log.error("User with username {} not found", username);

      throw new UserNotFoundException(username);
    }

    User userForChange = user.get();
    userForChange.setPassword(passwordEncoder.encode(changePasswordDto.getPassword()));

    userRepository.save(userForChange);
  }

  @Override
  public Collection<UserAuthority> findAllAuthoritiesForUserId(final Long userId)
          throws UserService.UserNotFoundException {
    findById(userId);

    return userAuthorityRepository.findAllUserAuthorityByUserId(userId);
  }

  @Override
  public void deleteAuthority(
          final Long userId, final String userAuthorityString)
          throws UserService.UserNotFoundException, UserAuthorityNotFoundException {
    final User user = findById(userId);
    final Optional<UserAuthority> userAuthority =
            user.getAuthority(UserAuthorityEnum.valueOf(userAuthorityString));

    if (!userAuthority.isPresent()) {
      throw new UserAuthorityNotFoundException(userId, userAuthorityString);
    }

    final User userWithoutAuthority = user.removeUserAuthority(userAuthority.get());

    userRepository.save(userWithoutAuthority);
  }
}
