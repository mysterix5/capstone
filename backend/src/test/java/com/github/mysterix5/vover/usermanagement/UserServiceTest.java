package com.github.mysterix5.vover.usermanagement;

import com.github.mysterix5.vover.model.UserAuthenticationDTO;
import com.github.mysterix5.vover.model.VoverUser;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;


class UserServiceTest {

    @Test
    void shouldCreateNewUser() {
        // given
        UserAuthenticationDTO userCreationDTO = new UserAuthenticationDTO("testUser", "password");
        UserMongoRepository userRepository = Mockito.mock(UserMongoRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        Mockito.when(passwordEncoder.encode("password")).thenReturn("hashedPassword");
        Mockito.when(userRepository.existsByUsername("testUser")).thenReturn(false);
        UserService userService = new UserService(userRepository, passwordEncoder);

        // when
        userService.createUser(userCreationDTO);
        VoverUser expectedUser = new VoverUser();
        expectedUser.setUsername("testUser");
        expectedUser.setPassword("hashedPassword");
        expectedUser.addRole("user");

        // then
        Mockito.verify(userRepository).save(expectedUser);
    }
    @Test
    void shouldFailOnCreateNewUserBecauseUsernameIsBlankOrNull() {
        UserAuthenticationDTO userCreationDTO1 = new UserAuthenticationDTO("", "password");
        UserAuthenticationDTO userCreationDTO2 = new UserAuthenticationDTO(null, "password");
        UserMongoRepository userRepository = Mockito.mock(UserMongoRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        UserService userService = new UserService(userRepository, passwordEncoder);

        Assertions.assertThatExceptionOfType(BadCredentialsException.class)
                .isThrownBy(() -> userService.createUser(userCreationDTO1))
                .withMessage("username is blank");

        Assertions.assertThatExceptionOfType(BadCredentialsException.class)
                .isThrownBy(() -> userService.createUser(userCreationDTO2))
                .withMessage("username is blank");

    }

    @Test
    void shouldFailOnCreateNewUserBecauseUserAlreadyExists() {
        // given
        UserAuthenticationDTO userCreationDTO = new UserAuthenticationDTO("testUser", "password");
        UserMongoRepository userRepository = Mockito.mock(UserMongoRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        Mockito.when(userRepository.existsByUsername("testUser")).thenReturn(true);
        UserService userService = new UserService(userRepository, passwordEncoder);

        Assertions.assertThatExceptionOfType(BadCredentialsException.class)
                .isThrownBy(() -> userService.createUser(userCreationDTO))
                .withMessage("a user with this name already exists");
    }
}