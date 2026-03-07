package com.pornput.rbactemplate.jwt.implement;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jwt.*;
import com.pornput.rbactemplate.jwt.JwtClaims;
import com.pornput.rbactemplate.jwt.JwtConfig;
import com.pornput.rbactemplate.jwt.JwtException;
import com.pornput.rbactemplate.jwt.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Slf4j
@Service
public class JwtServiceImpl implements JwtService {

    private final JwtConfig jwtConfig;

    public JwtServiceImpl(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    @Override
    public String generateAccessToken(String subject, Map<String, Object> claims) {
        log.debug("Generating access token for subject: {}", subject);
        try {
            Instant now = Instant.now();
            Instant expiry = now.plusSeconds(jwtConfig.getExpiration());

            JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
                    .subject(subject)
                    .issuer(jwtConfig.getIssuer())
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(expiry));

            claims.forEach(builder::claim);

            SignedJWT jwt = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.HS256),
                    builder.build()
            );

            jwt.sign(new MACSigner(jwtConfig.getSecret()));
            log.info("Generated token");
            return jwt.serialize();

        } catch (Exception e) {
            log.error("Failed to generate access token for subject: {}", subject, e);
            throw new JwtException("Failed to generate JWT", e);
        }
    }

    @Override
    public String generateRefreshToken(String subject) {
        log.debug("Generating refresh token for subject: {}", subject);
        try {
            Instant now = Instant.now();
            Instant expiry = now.plusSeconds(jwtConfig.getExpirationRefresh());

            JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
                    .subject(subject)
                    .issuer(jwtConfig.getIssuer())
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(expiry));

            SignedJWT jwt = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.HS256),
                    builder.build()
            );

            jwt.sign(new MACSigner(jwtConfig.getSecretRefresh()));
            log.info("Generated Refresh token");
            return jwt.serialize();

        } catch (Exception e) {
            log.error("Failed to generate refresh token for subject: {}", subject, e);
            throw new JwtException("Failed to generate refresh JWT", e);
        }
    }

    @Override
    public JwtClaims verifyAccessToken(String token) {
        log.debug("Verifying access token");
        try {
            SignedJWT jwt = SignedJWT.parse(token);

            if (!jwt.verify(new MACVerifier(jwtConfig.getSecret()))) {
                log.error("Invalid JWT token");
                throw new JwtException("Invalid JWT signature");
            }

            JWTClaimsSet claims = jwt.getJWTClaimsSet();

            if (claims.getExpirationTime().before(new Date())) {
                log.error("Expired JWT token");
                throw new JwtException("JWT expired");
            }

            log.debug("Access token verified successfully for subject: {}", claims.getSubject());
            return new JwtClaims(
                    claims.getSubject(),
                    claims.getIssueTime().toInstant(),
                    claims.getExpirationTime().toInstant(),
                    claims.getClaims()
            );

        } catch (JwtException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to verify JWT token", e);
            throw new JwtException("Invalid JWT", e);
        }
    }

    @Override
    public String verifyRefreshToken(String token) {
        log.debug("Verifying refresh token");
        try {
            SignedJWT jwt = SignedJWT.parse(token);

            if (!jwt.verify(new MACVerifier(jwtConfig.getSecretRefresh()))) {
                log.error("Invalid refresh JWT token");
                throw new JwtException("Invalid refresh JWT signature");
            }

            JWTClaimsSet claims = jwt.getJWTClaimsSet();

            if (claims.getExpirationTime().before(new Date())) {
                log.error("Expired refresh JWT token");
                throw new JwtException("refresh JWT expired");
            }

            log.debug("Refresh token verified successfully for subject: {}", claims.getSubject());
            return claims.getSubject();

        } catch (JwtException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to verify JWT token", e);
            throw new JwtException("Invalid JWT", e);
        }
    }
}
