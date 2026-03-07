package com.pornput.rbactemplate.services;

import com.pornput.rbactemplate.constant.RbacConstant;
import com.pornput.rbactemplate.exception.BadRequestException;
import com.pornput.rbactemplate.exception.UnauthorizedException;
import com.pornput.rbactemplate.jwt.JwtConfig;
import com.pornput.rbactemplate.jwt.JwtService;
import com.pornput.rbactemplate.mapper.UserMapper;
import com.pornput.rbactemplate.model.rbac.CustomUserDetails;
import com.pornput.rbactemplate.model.rbac.request.LoginRequest;
import com.pornput.rbactemplate.model.rbac.request.RegisterRequest;
import com.pornput.rbactemplate.model.rbac.response.AccessTokenResponse;
import com.pornput.rbactemplate.model.rbac.response.RegisterResponse;
import com.pornput.rbactemplate.entities.Role;
import com.pornput.rbactemplate.entities.User;
import com.pornput.rbactemplate.repositories.RoleRepository;
import com.pornput.rbactemplate.repositories.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;
    private final JwtConfig jwtConfig;
    private final CustomUserDetailsService customUserDetailsService;

    private final UserMapper userMapper;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        log.info("Processing registration request for username: {}", request.getUsername());

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            log.warn("Registration failed - username already exists: {}", request.getUsername());
            throw new BadRequestException("Username already exists");
        }

//        TODO: can register with roleType from request
        Role userRole = roleRepository.findByName(RbacConstant.USER)
                .orElseThrow(() -> {
                    log.error("Role '{}' not found in database", RbacConstant.USER);
                    return new IllegalStateException("Username not found");
                });

        String encodedPassword =
                passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .username(request.getUsername())
                .password(encodedPassword)
                .role(userRole)
                .enabled(Boolean.TRUE)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully - userId: {}, username: {}", savedUser.getId(), savedUser.getUsername());

        return userMapper.mapRegisterResponse(savedUser);
    }

    public AccessTokenResponse login(LoginRequest request, HttpServletResponse response) {
        log.info("Processing login request for username: {}", request.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        log.debug("Authentication successful for user: {}", request.getUsername());

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        log.info("Username: {}", userDetails.getUsername());

        String accessToken = jwtService.generateAccessToken(
                userDetails.getUsername(),
                Map.of(
                        "role", userDetails.getAuthorities().stream()
                                .findFirst()
                                .map(GrantedAuthority::getAuthority).orElse(Strings.EMPTY)
                )
        );

        String refreshToken = jwtService.generateRefreshToken(userDetails.getUsername());
        log.debug("Access token and refresh token generated for user: {}", userDetails.getUsername());

        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setMaxAge(jwtConfig.getExpirationRefresh());
        // WARN: on production should enable secure
        // cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        response.addCookie(cookie);
        log.info("Login successful for user: {}", userDetails.getUsername());

        return AccessTokenResponse.builder()
                .accessToken(accessToken)
                .build();
    }

    public AccessTokenResponse refresh(String refreshToken) {
        log.info("Processing token refresh request");

        String username = jwtService.verifyRefreshToken(refreshToken);
        if (Objects.isNull(username)) {
            log.warn("Token refresh failed - invalid refresh token");
            throw new UnauthorizedException("Invalid refresh token");
        }

        log.debug("Refresh token verified for user: {}", username);
        CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(username);

        String accessToken = jwtService.generateAccessToken(
                userDetails.getUsername(),
                Map.of(
                        "role", userDetails.getAuthorities().stream()
                                .findFirst()
                                .map(GrantedAuthority::getAuthority).orElse(Strings.EMPTY)
                )
        );

        log.info("Token refreshed successfully for user: {}", username);
        return AccessTokenResponse.builder()
                .accessToken(accessToken)
                .build();
    }
}
