package com.pornput.rbactemplate.services;

import com.pornput.rbactemplate.constants.RbacConstant;
import com.pornput.rbactemplate.exceptions.BadRequestException;
import com.pornput.rbactemplate.jwt.JwtService;
import com.pornput.rbactemplate.mapper.UserMapper;
import com.pornput.rbactemplate.model.rbac.CustomUserDetails;
import com.pornput.rbactemplate.model.rbac.request.LoginRequest;
import com.pornput.rbactemplate.model.rbac.request.RegisterRequest;
import com.pornput.rbactemplate.model.rbac.response.LoginResponse;
import com.pornput.rbactemplate.model.rbac.response.RegisterResponse;
import com.pornput.rbactemplate.model.rbac.Role;
import com.pornput.rbactemplate.model.rbac.User;
import com.pornput.rbactemplate.repositories.RoleRepository;
import com.pornput.rbactemplate.repositories.UserRepository;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            log.error("Username already exists");
            throw new BadRequestException("Username already exists");
        }

        Role userRole = roleRepository.findByName(RbacConstant.USER)
                .orElseThrow(() -> new IllegalStateException("Username not found"));

        String encodedPassword =
                passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .username(request.getUsername())
                .password(encodedPassword)
                .role(userRole)
                .enabled(Boolean.TRUE)
                .build();

        return UserMapper.MAPPER.mapRegisterResponse(userRepository.save(user));
    }

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        log.info("Authentication: {}", authentication);

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        log.info("Username: {}", userDetails.getUsername());

        String token = jwtService.generateToken(
                userDetails.getUsername(),
                Map.of(
                        "role", userDetails.getAuthorities().stream()
                                .findFirst()
                                .map(GrantedAuthority::getAuthority).orElse(Strings.EMPTY)
                )
        );

        return LoginResponse.builder()
                .accessToken(token)
                .build();
    }
}
