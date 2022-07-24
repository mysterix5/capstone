package com.github.mysterix5.vover.usermanagement;

import com.github.mysterix5.vover.model.MultipleSubErrorException;
import com.github.mysterix5.vover.model.UserRegisterDTO;
import com.github.mysterix5.vover.model.VoverUser;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.springframework.security.crypto.password.PasswordEncoder;


class UserServiceTest {

    UserMongoRepository mockedUserRepository;
    PasswordEncoder mockedPasswordEncoder;
    PasswordValidator mockedPasswordValidator;
    UserService userService;

    @BeforeEach
    void setupUserService() {
        mockedUserRepository = Mockito.mock(UserMongoRepository.class);
        mockedPasswordEncoder = Mockito.mock(PasswordEncoder.class);
        mockedPasswordValidator = Mockito.mock(PasswordValidator.class);
        userService = new UserService(mockedUserRepository, mockedPasswordEncoder, mockedPasswordValidator);
    }

    @Test
    void shouldCreateNewUser() {
        // given
        String username = "testUser";
        String password = "password";
        UserRegisterDTO userCreationDTO = new UserRegisterDTO(username, password, password);

        Mockito.when(mockedPasswordValidator.validate(Mockito.any(PasswordData.class))).thenReturn(new RuleResult(true));
        Mockito.when(mockedPasswordEncoder.encode(password)).thenReturn("hashedPassword");
        Mockito.when(mockedUserRepository.existsByUsername(username)).thenReturn(false);

        // when
        userService.createUser(userCreationDTO);

        VoverUser expectedUser = new VoverUser();
        expectedUser.setUsername(username);
        expectedUser.setPassword("hashedPassword");
        expectedUser.addRole("user");

        // then
        Mockito.verify(mockedUserRepository).save(expectedUser);
    }
    @Test
    void shouldFailOnCreateNewUserBecausePasswordIsInvalid() {
        // given
        String username = "testUser";
        String password = "password";
        UserRegisterDTO userCreationDTO = new UserRegisterDTO(username, password, password);

        Mockito.when(mockedPasswordValidator.validate(Mockito.any(PasswordData.class))).thenReturn(new RuleResult(false));
        Mockito.when(mockedUserRepository.existsByUsername(username)).thenReturn(false);

        // when
        Assertions.assertThatExceptionOfType(MultipleSubErrorException.class)
                .isThrownBy(() -> userService.createUser(userCreationDTO))
                .withMessage("Your password is not secure enough");
    }
    @Test
    void shouldFailOnCreateNewUserBecauseUsernameIsBlankOrNull() {
        UserRegisterDTO userCreationDTO1 = new UserRegisterDTO("", "password", "password");
        UserRegisterDTO userCreationDTO2 = new UserRegisterDTO(null, "password", "password");

        Assertions.assertThatExceptionOfType(MultipleSubErrorException.class)
                .isThrownBy(() -> userService.createUser(userCreationDTO1))
                .withMessage("username is blank");

        Assertions.assertThatExceptionOfType(MultipleSubErrorException.class)
                .isThrownBy(() -> userService.createUser(userCreationDTO2))
                .withMessage("username is blank");

    }

    @Test
    void shouldFailOnCreateNewUserBecauseUserAlreadyExists() {
        // given
        UserRegisterDTO userCreationDTO = new UserRegisterDTO("testUser", "password", "password");

        Mockito.when(mockedUserRepository.existsByUsername("testUser")).thenReturn(true);

        Assertions.assertThatExceptionOfType(MultipleSubErrorException.class)
                .isThrownBy(() -> userService.createUser(userCreationDTO))
                .withMessage("a user with this name already exists");
    }
}