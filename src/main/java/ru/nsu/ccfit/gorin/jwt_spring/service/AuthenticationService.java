package ru.nsu.ccfit.gorin.jwt_spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.nsu.ccfit.gorin.jwt_spring.dto.AuthRequestDTO;
import ru.nsu.ccfit.gorin.jwt_spring.dto.AuthenticationResponseDTO;
import ru.nsu.ccfit.gorin.jwt_spring.dto.RegisterRequestDTO;
import ru.nsu.ccfit.gorin.jwt_spring.model.User;
import ru.nsu.ccfit.gorin.jwt_spring.repository.UserRepository;
import ru.nsu.ccfit.gorin.jwt_spring.security.JwtService;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;


    public AuthenticationResponseDTO register(RegisterRequestDTO registerDTO) {
        User user = User.builder()
                .firstname(registerDTO.getFirstname())
                .lastname(registerDTO.getLastname())
                .email(registerDTO.getEmail())
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .build();

        userRepository.save(user);

        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponseDTO.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponseDTO authenticate(AuthRequestDTO authDTO) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authDTO.getEmail(),
                        authDTO.getPassword()
                )
        );

        User user = userRepository.findByEmail(authDTO.getEmail())
                .orElseThrow();

        String jwtToken = jwtService.generateToken(user);

        return AuthenticationResponseDTO.builder()
                .token(jwtToken)
                .build();
    }
}
