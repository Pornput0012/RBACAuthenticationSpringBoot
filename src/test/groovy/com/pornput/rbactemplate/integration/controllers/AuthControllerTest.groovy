package com.pornput.rbactemplate.integration.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.pornput.rbactemplate.config.CorsProperties
import com.pornput.rbactemplate.controllers.AuthController
import com.pornput.rbactemplate.exception.BadRequestException
import com.pornput.rbactemplate.exception.UnauthorizedException
import com.pornput.rbactemplate.jwt.JwtConfig
import com.pornput.rbactemplate.jwt.JwtService
import com.pornput.rbactemplate.mapper.UserMapper
import com.pornput.rbactemplate.model.rbac.request.LoginRequest
import com.pornput.rbactemplate.model.rbac.request.RegisterRequest
import com.pornput.rbactemplate.model.rbac.response.AccessTokenResponse
import com.pornput.rbactemplate.model.rbac.response.RegisterResponse
import com.pornput.rbactemplate.repositories.RoleRepository
import com.pornput.rbactemplate.repositories.UserRepository
import com.pornput.rbactemplate.services.AuthService
import com.pornput.rbactemplate.services.CustomUserDetailsService
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.web.bind.MethodArgumentNotValidException
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

@WebMvcTest(AuthController)
@EnableConfigurationProperties(CorsProperties.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest extends Specification {

    private static final String API_AUTH_LOGIN = "/api/auth/login"
    private static final String API_AUTH_REFRESH = "/api/auth/refresh"
    private static final String API_AUTH_REGISTER = "/api/auth/register"

    @Autowired
    MockMvc mockMvc

    @Autowired
    ObjectMapper objectMapper

    @SpringBean
    AuthService authService = Mock()

    @MockBean
    UserRepository userRepository

    @MockBean
    RoleRepository roleRepository

    @MockBean
    PasswordEncoder passwordEncoder

    @MockBean
    AuthenticationManager authenticationManager

    @SpringBean
    JwtService jwtService = Mock()

    @MockBean
    JwtConfig jwtConfig

    @MockBean
    CustomUserDetailsService customUserDetailsService

    @MockBean
    UserMapper userMapper

    def "should return 200 when register request is valid"() {
        given:
        def requestBody = [
                username       : "Pornput",
                password       : "12345678",
                confirmPassword: "12345678"
        ]

        def response = RegisterResponse.builder()
                .username("Pornput")
                .build()

        authService.register(_ as RegisterRequest) >> response

        when:
        def mvcResult = mockMvc.perform(
                post(API_AUTH_REGISTER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody))
        ).andReturn()

        then:
        mvcResult.response.status == 200
    }

    def "should return 200 when login request is valid"() {
        given:
        def requestBody = [
                username: "Pornput",
                password: "12345678"
        ]

        def response = AccessTokenResponse.builder()
                .accessToken("test_access_token")
                .build()

        authService.login(_ as LoginRequest, _ as HttpServletResponse) >> response

        when:
        def mvcResult = mockMvc.perform(
                post(API_AUTH_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody))
        ).andReturn()

        then:
        mvcResult.response.status == 200
    }

    def "should return 200 when refresh request is valid"() {
        given:
        def response = AccessTokenResponse.builder()
                .accessToken("test_access_token")
                .build()

        authService.refresh(_ as String) >> response

        when:
        def mvcResult = mockMvc.perform(
                post(API_AUTH_REFRESH)
                        .cookie(new Cookie("refreshToken", "test_refresh_token"))
        ).andReturn()

        then:
        mvcResult.response.status == 200
    }

    def "should return 400 when login request is invalid"() {
        given:
        def request = [
                username: "",
                password: ""
        ]

        when:
        def mvcResult = mockMvc.perform(
                post(API_AUTH_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andReturn()

        then:
        mvcResult.response.status == 400
        mvcResult.resolvedException != null
        mvcResult.resolvedException instanceof MethodArgumentNotValidException
    }

    def "should return 400 when register request is invalid"() {
        given:
        def request = [
                username       : "",
                password       : "",
                confirmPassword: ""
        ]

        when:
        def mvcResult = mockMvc.perform(
                post(API_AUTH_REGISTER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andReturn()

        then:
        mvcResult.response.status == 400
        mvcResult.resolvedException != null
        mvcResult.resolvedException instanceof MethodArgumentNotValidException
    }

    def "should return 400 when register user exists"() {
        given:
        def request = [
                username       : "Pornput",
                password       : "12345678",
                confirmPassword: "12345678"
        ]

        1 * authService.register(_ as RegisterRequest) >> {
            throw new BadRequestException("Username already exists")
        }

        when:
        def mvcResult = mockMvc.perform(
                post(API_AUTH_REGISTER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andReturn()

        then:
        mvcResult.response.status == 400
        mvcResult.resolvedException != null
        mvcResult.resolvedException instanceof BadRequestException
    }

    def "should return 401 when verify refresh token is fail"() {
        given:
        def response = AccessTokenResponse.builder()
                .accessToken("test_access_token")
                .build()

        authService.refresh(_ as String) >> {
            throw new UnauthorizedException("Invalid refresh token")
        }

        when:
        def mvcResult = mockMvc.perform(
                post(API_AUTH_REFRESH)
                        .cookie(new Cookie("refreshToken", "test_refresh_token"))
        ).andReturn()

        then:
        mvcResult.response.status == 401
        mvcResult.resolvedException != null
        mvcResult.resolvedException instanceof UnauthorizedException
    }

}