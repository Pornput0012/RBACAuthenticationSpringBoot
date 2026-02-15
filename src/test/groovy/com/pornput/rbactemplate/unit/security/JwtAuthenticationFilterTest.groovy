package com.pornput.rbactemplate.unit.security

import com.pornput.rbactemplate.constant.RbacConstant
import com.pornput.rbactemplate.jwt.JwtClaims
import com.pornput.rbactemplate.jwt.JwtPrincipal
import com.pornput.rbactemplate.jwt.JwtService
import com.pornput.rbactemplate.security.JwtAuthenticationFilter
import com.pornput.rbactemplate.services.CustomUserDetailsService
import jakarta.servlet.FilterChain
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import spock.lang.Specification

class JwtAuthenticationFilterTest extends Specification {

    // 1. จำลอง Service แบบ Dynamic Typing (def)
    def jwtService = Mock(JwtService)
    def userDetailsService = Mock(CustomUserDetailsService)
    def filterChain = Mock(FilterChain)

    // 2. สร้าง Filter
    def filter = new JwtAuthenticationFilter(jwtService, userDetailsService)

    // 3. เตรียม Request / Response
    def request = new MockHttpServletRequest()
    def response = new MockHttpServletResponse()

    def setup() {
        // เคลียร์ SecurityContext ก่อนเริ่มทุกเทส
        SecurityContextHolder.clearContext()
    }

    def cleanup() {
        // เคลียร์ทิ้งอีกรอบหลังจบเทส ป้องกันของหลุดไปกวนเทสอื่น
        SecurityContextHolder.clearContext()
    }

    def "should do nothing and continue filter chain if no Authorization header"() {
        when: "เรียกใช้งาน filter โดยไม่มี Header"
        filter.doFilterInternal(request, response, filterChain)

        then: "ไม่เรียกใช้ jwtService และข้ามไปทำ filter ตัวถัดไปเลย"
        0 * jwtService.verifyAccessToken(_)
        1 * filterChain.doFilter(request, response)

        and: "SecurityContext ต้องไม่มี Authentication"
        SecurityContextHolder.context.authentication == null
    }

    def "should authenticate user and set SecurityContext if valid Bearer token is provided"() {
        given: "มี Token ที่ถูกต้องส่งเข้ามา"
        def token = "valid_token_123"
        request.addHeader("Authorization", "Bearer " + token)

        and: "จำลองผลลัพธ์จาก JwtService ให้คืนค่า Claims กลับมา (ใช้ def เช่นกัน)"
        def mockClaims = Mock(JwtClaims)
        mockClaims.getSubject() >> "Pornput"
        mockClaims.getClaims() >> [(RbacConstant.CLAIM_ROLE): "ROLE_USER"]

        1 * jwtService.verifyAccessToken(token) >> mockClaims

        when:
        filter.doFilterInternal(request, response, filterChain)

        then: "ต้องข้ามไปทำ filter ตัวถัดไป"
        1 * filterChain.doFilter(request, response)

        and: "SecurityContext ต้องมีข้อมูลของ Pornput และ Role ที่ถูกต้อง"
        def auth = SecurityContextHolder.context.authentication
        auth != null

        def principal = auth.principal as JwtPrincipal
        principal.username == "Pornput"
        principal.role == "ROLE_USER"

        auth.authorities.size() == 1
        auth.authorities[0].authority == "ROLE_USER"
    }

    def "should clear SecurityContext if token verification throws an Exception"() {
        given: "มี Token ปลอมหรือหมดอายุส่งเข้ามา"
        def token = "invalid_token_999"
        request.addHeader("Authorization", "Bearer " + token)

        and: "จำลองให้ JwtService โยน Exception แตกออกมา"
        1 * jwtService.verifyAccessToken(token) >> {
            throw new RuntimeException("Token expired or invalid!")
        }

        when:
        filter.doFilterInternal(request, response, filterChain)

        then: "ต้องข้ามไปทำ filter ตัวถัดไปอยู่ดี"
        1 * filterChain.doFilter(request, response)

        and: "SecurityContext ต้องว่างเปล่า (โดน clearContext() ทิ้ง)"
        SecurityContextHolder.context.authentication == null
    }
}