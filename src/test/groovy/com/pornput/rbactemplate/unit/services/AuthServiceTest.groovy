package com.pornput.rbactemplate.unit.services

import com.pornput.rbactemplate.constant.RbacConstant
import com.pornput.rbactemplate.exception.BadRequestException
import com.pornput.rbactemplate.exception.UnauthorizedException
import com.pornput.rbactemplate.jwt.JwtConfig
import com.pornput.rbactemplate.jwt.JwtService
import com.pornput.rbactemplate.mapper.UserMapper
import com.pornput.rbactemplate.model.rbac.CustomUserDetails
import com.pornput.rbactemplate.entities.Role
import com.pornput.rbactemplate.entities.User
import com.pornput.rbactemplate.model.rbac.request.LoginRequest
import com.pornput.rbactemplate.model.rbac.request.RegisterRequest
import com.pornput.rbactemplate.model.rbac.response.RegisterResponse
import com.pornput.rbactemplate.repositories.RoleRepository
import com.pornput.rbactemplate.repositories.UserRepository
import com.pornput.rbactemplate.services.AuthService
import com.pornput.rbactemplate.services.CustomUserDetailsService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.Specification

class AuthServiceTest extends Specification {
    def userRepository = Mock(UserRepository)
    def roleRepository = Mock(RoleRepository)
    def passwordEncoder = Mock(PasswordEncoder)
    def authenticationManager = Mock(AuthenticationManager)
    def jwtService = Mock(JwtService)
    def jwtConfig = Mock(JwtConfig)
    def customUserDetailsService = Mock(CustomUserDetailsService)
    def userMapper = Mock(UserMapper)

    def authService = new AuthService(
            userRepository,
            roleRepository,
            passwordEncoder,
            authenticationManager,
            jwtService,
            jwtConfig,
            customUserDetailsService,
            userMapper
    )

//    <methodName> should <expected behavior> when <condition>

    def "register should successfully when request is valid"() {
        given:
        def request = RegisterRequest.builder()
                .username("Pornput")
                .password("12345678")
                .confirmPassword("12345678")
                .build()

        def role = new Role(name: RbacConstant.USER)
        def savedUser = User.builder()
                .username("Pornput")
                .build()

        userRepository.findByUsername("Pornput") >> Optional.empty()
        roleRepository.findByName(RbacConstant.USER) >> Optional.of(role)
        passwordEncoder.encode("12345678") >> "encoded-password"
        userRepository.save(_ as User) >> savedUser

        userMapper.mapRegisterResponse(savedUser) >>
                RegisterResponse.builder()
                        .username("Pornput")
                        .build()

        when:
        def result = authService.register(request)

        then:
        result.username == "Pornput"
    }

    def "register should throw BadRequestException when username already exists"() {
        given:
        def request = RegisterRequest.builder()
                .username("Pornput")
                .password("12345678")
                .confirmPassword("12345678")
                .build()

        userRepository.findByUsername("Pornput") >> Optional.of(User.builder()
                .username("Pornput")
                .build())

        when:
        authService.register(request)

        then:
        def ex = thrown(BadRequestException)
        ex.getMessage() == "Username already exists"
    }


    def "login should return access token and set refresh cookie when credentials are valid"() {
        given:
        def request = LoginRequest.builder()
                .username("Pornput")
                .password("12345678")
                .build()

        def response = Mock(HttpServletResponse)

        def userDetails = Mock(CustomUserDetails) {
            getUsername() >> "Pornput"
            getAuthorities() >> [new SimpleGrantedAuthority("USER")]
        }

        def authentication = Mock(Authentication) {
            getPrincipal() >> userDetails
        }

        authenticationManager.authenticate(_ as UsernamePasswordAuthenticationToken) >>
                authentication

        jwtService.generateAccessToken("Pornput", [role: "USER"]) >>
                "access-token"

        jwtService.generateRefreshToken("Pornput") >>
                "refresh-token"

        jwtConfig.getExpirationRefresh() >> 3600

        when:
        def result = authService.login(request, response)

        then:
        result.accessToken == "access-token"

        1 * response.addCookie({
            it.name == "refreshToken" &&
                    it.value == "refresh-token" &&
                    it.httpOnly &&
                    it.path == "/"
        })
    }

    def "refresh should return new access token when refresh token is valid"() {
        given:
        def refreshToken = "valid-refresh-token"

        jwtService.verifyRefreshToken(refreshToken) >> "Pornput"

        def userDetails = Mock(CustomUserDetails) {
            getUsername() >> "Pornput"
            getAuthorities() >> [new SimpleGrantedAuthority("USER")]
        }

        customUserDetailsService.loadUserByUsername("Pornput") >>
                userDetails

        jwtService.generateAccessToken("Pornput", [role: "USER"]) >>
                "new-access-token"

        when:
        def result = authService.refresh(refreshToken)

        then:
        result.accessToken == "new-access-token"
    }

    def "refresh should throw UnauthorizedException when refresh token is invalid"() {
        given:
        jwtService.verifyRefreshToken("invalid-token") >> null

        when:
        authService.refresh("invalid-token")

        then:
        def ex = thrown(UnauthorizedException)
        ex.message == "Invalid refresh token"
    }

}
