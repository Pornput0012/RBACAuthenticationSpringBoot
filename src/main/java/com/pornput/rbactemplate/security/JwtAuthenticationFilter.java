package com.pornput.rbactemplate.security;

import com.pornput.rbactemplate.constant.RbacConstant;
import com.pornput.rbactemplate.jwt.JwtClaims;
import com.pornput.rbactemplate.jwt.JwtPrincipal;
import com.pornput.rbactemplate.jwt.JwtService;
import com.pornput.rbactemplate.services.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.trace("No Bearer token found in request to: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        log.debug("Processing JWT authentication for request: {}", request.getRequestURI());

        try {
            JwtClaims claims = jwtService.verifyAccessToken(token);

            if (Objects.isNull(SecurityContextHolder.getContext().getAuthentication())) {

                JwtPrincipal principal = JwtPrincipal.builder()
                        .username(claims.getSubject())
                        .role((String) claims.getClaims().get(RbacConstant.CLAIM_ROLE))
                        .build();

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                principal,
                                null,
                                List.of(new SimpleGrantedAuthority(principal.getRole()))
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);

                log.debug("JWT authentication set for user: {}, role: {}", principal.getUsername(), principal.getRole());
            }

        } catch (Exception ex) {
            log.error("JWT authentication failed for request [{}]: {}", request.getRequestURI(), ex.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}