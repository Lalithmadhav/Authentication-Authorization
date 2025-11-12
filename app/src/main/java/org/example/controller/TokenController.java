package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.entities.RefreshToken;
import org.example.exception.TokenRefreshException;
import org.example.request.AuthRequestDTO;
import org.example.request.RefreshTokenRequestDTO;
import org.example.response.JWTResponseDTO;
import org.example.service.JWTService;
import org.example.service.RefreshTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/v1")
@RequiredArgsConstructor
public class TokenController {

    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final JWTService jwtService;

    @PostMapping("/login")
    public ResponseEntity<JWTResponseDTO> authenticateAndGetToken(@RequestBody AuthRequestDTO authRequestDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authRequestDTO.getUserName(),
                    authRequestDTO.getPassword()
            ));
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequestDTO.getUserName());
        String accessToken = jwtService.generateToken(authRequestDTO.getUserName());

        JWTResponseDTO response = JWTResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();

        return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            return new ResponseEntity("Invalid username/password", HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/refresh-token")
    public JWTResponseDTO refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {
        return refreshTokenService.findByToken(refreshTokenRequestDTO.getToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUserInfo)
                .map(userInfo -> {
                    String accessToken = jwtService.generateToken(userInfo.getUserName());
                    return JWTResponseDTO.builder()
                            .accessToken(accessToken)
                            .refreshToken(refreshTokenRequestDTO.getToken())
                            .build();
                })
                .orElseThrow(() -> new TokenRefreshException("Refresh token is not in database or is invalid!"));
    }
}
