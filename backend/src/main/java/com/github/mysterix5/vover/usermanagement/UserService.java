package com.github.mysterix5.vover.usermanagement;

import com.github.mysterix5.vover.model.VoverUser;
import com.github.mysterix5.vover.model.UserAuthenticationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
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
public class UserService  implements UserDetailsService {
    private final UserMongoRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void createUser(UserAuthenticationDTO userCreationDTO) {
        if(userCreationDTO.getUsername()==null || userCreationDTO.getUsername().isBlank()){
            throw new BadCredentialsException("username is blank");
        }
        if(userRepository.existsByUsername(userCreationDTO.getUsername())){
            throw new BadCredentialsException("a user with this name already exists");
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
                .map(user->new User(user.getUsername(), user.getPassword(), List.of()))
                .orElseThrow(()->new UsernameNotFoundException(username + " not found"));
    }

    public Optional<VoverUser> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

}
