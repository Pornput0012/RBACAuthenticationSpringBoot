package com.pornput.rbactemplate.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.pornput.rbactemplate.model.rbac.request.LoginRequest
import com.pornput.rbactemplate.model.rbac.request.RegisterRequest
import com.pornput.rbactemplate.model.rbac.response.AccessTokenResponse
import com.pornput.rbactemplate.model.rbac.response.RegisterResponse
import com.pornput.rbactemplate.services.AuthService
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.extension.MediaType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

@SpringBootTest
class AuthControllerTest extends Specification {

    @Autowired
    WebApplicationContext webApplicationContext
    @Autowired
    ObjectMapper objectMapper

    MockMvc mockMvc

    @MockBean
    AuthService authService

    HttpServletResponse httpServletResponse

    def setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
    }

    def "should return 400 when login request is invalid"() {
        given:
        def request = [
                username: "",
                password: ""
        ]

        when:
        def mvcResult = mockMvc.perform(
                post("/api/auth/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON.toString())
                        .accept(MediaType.APPLICATION_JSON.toString())
        ).andReturn()

        then:
        mvcResult.response.status == 400
    }

    def "should return 400 when register request is invalid"() {
        given:
        def request = [
                username       : "",
                password       : "",
                passwordConfirm: "",
        ]

        when:
        def mvcResult = mockMvc.perform(
                post("/api/auth/register")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON.toString())
                        .accept(MediaType.APPLICATION_JSON.toString())
        ).andReturn()

        then:
        mvcResult.response.status == 400
    }

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
                post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON.toString())
                        .accept(MediaType.APPLICATION_JSON.toString())
                        .content(objectMapper.writeValueAsString(requestBody))
        ).andReturn()

        then:
        mvcResult.response.status == 200
    }

    def "should return 200 when login request is valid"() {
        given:
        def requestBody = [
                username: "Pornput",
                password: "12345678",
        ]

        def response = AccessTokenResponse.builder()
                .accessToken("test_access_token")
                .build()

        authService.login(_ as LoginRequest, httpServletResponse) >> response

        when:
        def mvcResult = mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON.toString())
                        .accept(MediaType.APPLICATION_JSON.toString())
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
                post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON.toString())
                        .accept(MediaType.APPLICATION_JSON.toString())
                        .cookie(new Cookie("refreshToken", "test_refresh_token"))
        ).andReturn()

        then:
        mvcResult.response.status == 200
    }

}
