package com.github.mysterix5.vover.usermanagement;

import com.github.mysterix5.vover.model.MultipleSubErrorException;
import com.github.mysterix5.vover.model.UserRegisterDTO;
import com.github.mysterix5.vover.model.VoverUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {
    private final UserMongoRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordValidator passwordValidator;

    public void createUser(UserRegisterDTO userCreationDTO) {
        if (userCreationDTO.getUsername() == null || userCreationDTO.getUsername().isBlank()) {
            throw new MultipleSubErrorException("username is blank");
        }
        if (userRepository.existsByUsername(userCreationDTO.getUsername())) {
            throw new MultipleSubErrorException("a user with this name already exists");
        }
        RuleResult passwordValidationResult = passwordValidator.validate(new PasswordData(userCreationDTO.getUsername(), userCreationDTO.getPassword()));
        if (!passwordValidationResult.isValid()) {
            throw new MultipleSubErrorException("Your password is not secure enough", passwordValidator.getMessages(passwordValidationResult));
        }
        if (!userCreationDTO.getPassword().equals(userCreationDTO.getPasswordRepeat())){
            throw new MultipleSubErrorException("Passwords have to match");
        }

        VoverUser user = new VoverUser();
        user.setUsername(userCreationDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userCreationDTO.getPassword()));
        user.setRoles(List.of("user"));
        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findByUsername(username)
                .map(user -> new User(user.getUsername(), user.getPassword(), List.of()))
                .orElseThrow(() -> new UsernameNotFoundException(username + " not found"));
    }

    public Optional<VoverUser> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

}
