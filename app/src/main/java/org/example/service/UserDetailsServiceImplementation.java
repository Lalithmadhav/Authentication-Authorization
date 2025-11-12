package org.example.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.entities.UserInfo;
import org.example.exception.UsernameAlreadyExistsException;
import org.example.model.UserInfoDto;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImplementation implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserInfo user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return new CustomUserDetails(user);
    }

//    public UserInfo checkIfUserAlreadyExists(UserInfoDto userInfoDto) {
//        return UserRepository.findByUserName(userInfoDto.getUserName());
//    }

    public UserInfo signUpUser(UserInfoDto userInfoDto) {
        if (userRepository.existsByUserName(userInfoDto.getUserName())) {
            throw new UsernameAlreadyExistsException("Username already exists: " + userInfoDto.getUserName());
        }
        UserInfo newUser = new UserInfo();

        newUser.setUserId(UUID.randomUUID().toString());
        newUser.setUserName(userInfoDto.getUserName());
        newUser.setPassword(passwordEncoder.encode(userInfoDto.getPassword()));
        newUser.setRoles(new HashSet<>());

        System.out.println("--- ENTITY USERNAME BEFORE SAVE: " + newUser.getUserName() + " ---");

        return userRepository.save(newUser);
    }
}
