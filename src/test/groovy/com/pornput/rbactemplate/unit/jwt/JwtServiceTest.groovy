package com.pornput.rbactemplate.unit.jwt

import com.pornput.rbactemplate.jwt.JwtConfig
import com.pornput.rbactemplate.jwt.JwtException
import com.pornput.rbactemplate.jwt.implement.JwtServiceImpl
import spock.lang.Specification

import java.time.Instant

class JwtServiceTest extends Specification {

    def jwtConfig = Stub(JwtConfig) {
        getSecret() >> "super-secret-key-super-secret-key"
        getExpiration() >> 3600
        getIssuer() >> "test-issuer"
        getExpirationRefresh() >> 604800
        getSecretRefresh() >> "super-secret-key-super-refresh-key"
    }

    def jwtService = new JwtServiceImpl(jwtConfig)

    def "should verify access token successfully when token is valid"() {
        given:
        def subject = "Pornput"
        def claims = [role: "ROLE_USER"]

        when:
        def token = jwtService.generateAccessToken(subject, claims)
        def result = jwtService.verifyAccessToken(token)

        then:
        verifyAll(result) {
            subject == "Pornput"
            expiresAt.isAfter(Instant.now())
            claims.get("role") == "ROLE_USER"
        }
    }

    def "should verify refresh token successfully when token is valid"() {
        given:
        def subject = "Pornput"

        when:
        def token = jwtService.generateRefreshToken(subject)
        def result = jwtService.verifyRefreshToken(token)

        then:
        result == "Pornput"
    }

    def "should throw exception when generating access token with short secret"() {
        given:
        def jwtConfig = Stub(JwtConfig) {
            getSecret() >> "key-less-than-256-byte"
            getExpiration() >> 3600
            getIssuer() >> "test-issuer"
        }
        def jwtService = new JwtServiceImpl(jwtConfig)

        def subject = "Pornput"
        def claims = [role: "ROLE_USER"]

        when:
        jwtService.generateAccessToken(subject, claims)

        then:
        thrown(JwtException)
    }

    def "should throw exception when generating refresh token with short secret"() {
        given:
        def jwtConfig = Stub(JwtConfig) {
            getSecretRefresh() >> "key-less-than-256-byte"
            getExpirationRefresh() >> 3600
            getIssuer() >> "test-issuer"
        }
        def jwtService = new JwtServiceImpl(jwtConfig)

        def subject = "Pornput"

        when:
        jwtService.generateRefreshToken(subject)

        then:
        thrown(JwtException)
    }

    def "should throw exception when verifying access token with invalid token"() {
        given:
        def jwtConfig = Stub(JwtConfig) {
            getSecret() >> "key-less-than-256-byte"
            getExpiration() >> 3600
            getIssuer() >> "test-issuer"
        }
        def jwtService = new JwtServiceImpl(jwtConfig)


        when:
        jwtService.verifyAccessToken("test_access_token")

        then:
        thrown(JwtException)
    }

    def "should throw exception when verifying refresh token with invalid token"() {
        given:
        def jwtConfig = Stub(JwtConfig) {
            getSecretRefresh() >> "key-less-than-256-byte"
            getExpirationRefresh() >> 3600
            getIssuer() >> "test-issuer"
        }
        def jwtService = new JwtServiceImpl(jwtConfig)

        when:
        jwtService.verifyRefreshToken("test_refresh_token")

        then:
        thrown(JwtException)
    }

    def "should throw exception when verifying access token with different secret"() {
        given:
        def validConfig = Stub(JwtConfig) {
            getSecret() >> "0123456789abcdef0123456789abcdef"
            getExpiration() >> 3600
            getIssuer() >> "test-issuer"
        }

        def invalidConfig = Stub(JwtConfig) {
            getSecret() >> "ffffffffffffffffffffffffffffffff"
            getExpiration() >> 3600
            getIssuer() >> "test-issuer"
        }

        def jwtServiceForGenerate = new JwtServiceImpl(validConfig)
        def jwtServiceForVerify = new JwtServiceImpl(invalidConfig)

        def subject = "Pornput"
        def claims = [role: "ROLE_USER"]

        when:
        def token = jwtServiceForGenerate.generateAccessToken(subject, claims)
        jwtServiceForVerify.verifyAccessToken(token)

        then:
        thrown(JwtException)
    }

    def "should throw exception when verifying refresh token with different secret"() {
        given:
        def validConfig = Stub(JwtConfig) {
            getSecretRefresh() >> "0123456789abcdef0123456789abcdef"
            getIssuer() >> "test-issuer"
        }

        def invalidConfig = Stub(JwtConfig) {
            getSecretRefresh() >> "ffffffffffffffffffffffffffffffff"
            getIssuer() >> "test-issuer"
        }

        def jwtServiceForGenerate = new JwtServiceImpl(validConfig)
        def jwtServiceForVerify = new JwtServiceImpl(invalidConfig)

        def subject = "Pornput"

        when:
        def token = jwtServiceForGenerate.generateRefreshToken(subject)
        jwtServiceForVerify.verifyRefreshToken(token)

        then:
        thrown(JwtException)
    }

    def "should throw exception when access token is expired"() {
        given:
        def expiredConfig = Stub(JwtConfig) {
            getSecret() >> "0123456789abcdef0123456789abcdef"
            getExpiration() >> 1
            getIssuer() >> "test-issuer"
            getSecretRefresh() >> "0123456789abcdef0123456789abcdef"
            getExpirationRefresh() >> 1
        }

        def subject = "Pornput"
        def claims = [role: "ROLE_USER"]

        def shortLivedJwtService = new JwtServiceImpl(expiredConfig)

        def token = shortLivedJwtService.generateAccessToken(subject, claims)

        sleep(1500)

        when:
        shortLivedJwtService.verifyAccessToken(token)

        then:
        thrown(JwtException)
    }

    def "should throw exception when refresh token is expired"() {
        given:
        def expiredConfig = Stub(JwtConfig) {
            getIssuer() >> "test-issuer"
            getSecretRefresh() >> "0123456789abcdef0123456789abcdef"
            getExpirationRefresh() >> 1
        }

        def subject = "Pornput"

        def shortLivedJwtService = new JwtServiceImpl(expiredConfig)

        def token = shortLivedJwtService.generateRefreshToken(subject)

        sleep(1500)

        when:
        shortLivedJwtService.verifyRefreshToken(token)

        then:
        thrown(JwtException)
    }

}
