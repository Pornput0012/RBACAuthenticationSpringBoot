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

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Slf4j
@Service
public class JwtServiceImpl implements JwtService {

    private final JwtConfig config;
    private final byte[] secret;

    public JwtServiceImpl(JwtConfig config) {
        this.config = config;
        this.secret = config.getSecret().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String generateToken(String subject, Map<String, Object> claims) {
        try {
            Instant now = Instant.now();
            Instant expiry = now.plusSeconds(config.getExpiration());

            JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
                    .subject(subject)
                    .issuer(config.getIssuer())
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(expiry));

            claims.forEach(builder::claim);

            SignedJWT jwt = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.HS256),
                    builder.build()
            );

            jwt.sign(new MACSigner(secret));
            log.info("Generated token");
            return jwt.serialize();

        } catch (Exception e) {
            throw new JwtException("Failed to generate JWT", e);
        }
    }

    @Override
    public JwtClaims verify(String token) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);

            if (!jwt.verify(new MACVerifier(secret))) {
                log.error("Invalid JWT token");
                throw new JwtException("Invalid JWT signature");
            }

            JWTClaimsSet claims = jwt.getJWTClaimsSet();

            if (claims.getExpirationTime().before(new Date())) {
                log.error("Expired JWT token");
                throw new JwtException("JWT expired");
            }

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
}
