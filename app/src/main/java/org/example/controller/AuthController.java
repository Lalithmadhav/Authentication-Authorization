package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.entities.RefreshToken;
import org.example.model.UserInfoDto;
import org.example.response.JWTResponseDTO;
import org.example.service.JWTService;
import org.example.service.RefreshTokenService;
import org.example.service.UserDetailsServiceImplementation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/v1")
public class AuthController {

    private final JWTService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserDetailsServiceImplementation userDetailsServiceImplementation;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public JWTResponseDTO signUp(@RequestBody UserInfoDto userInfoDto) {
        System.out.println("___DTO USERNAME IN CONTROLLER: " + userInfoDto.getUserName() + "___");
        userDetailsServiceImplementation.signUpUser(userInfoDto);

        String username = userInfoDto.getUserName();
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(username);
        String jwtToken = jwtService.generateToken(username);

        return JWTResponseDTO.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }
}
