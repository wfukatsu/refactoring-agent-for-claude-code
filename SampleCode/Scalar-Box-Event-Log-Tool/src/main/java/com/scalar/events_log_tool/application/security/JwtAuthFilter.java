package com.scalar.events_log_tool.application.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Set<String> EXCLUDE_URLS = new HashSet<>(Arrays.asList(SecurityConfig.WHITE_LIST_URL));
    private static final String MESSAGE = "message";
    private static final String TOKEN_EXPIRED_MESSAGE = "Token Expired";
    private static final String INVALID_AUTHORIZATION_HEADER_MESSAGE = "Invalid Authorization Header";
    private static final String ILLEGAL_ARGUMENT_MESSAGE = "Illegal Argument while fetching the username";
    private static final String INVALID_TOKEN_MESSAGE = "Invalid Token";
    private static final String TOKEN_SIGNATURE_VALIDATION_FAILED_MESSAGE = "Invalid Token";
    private static final String INTERNAL_SEVER_ERROR_MESSAGE = "Internal Server Error";


    private final JwtHelper jwtHelper;
    private final UserDetailsService userDetailsService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public JwtAuthFilter(JwtHelper jwtHelper, UserDetailsService userDetailsService) {
        this.jwtHelper = jwtHelper;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestUri = request.getRequestURI();
        // Check if the requested URL is in the excluded set
        if (EXCLUDE_URLS.contains(requestUri)) {
            // Allow the request to proceed without authentication
            filterChain.doFilter(request, response);
            return;
        }

        String requestHeader = request.getHeader("Authorization");
        String username;
        String token;

        if (requestHeader == null || !requestHeader.startsWith("Bearer")) {
            response.addHeader(MESSAGE, INVALID_AUTHORIZATION_HEADER_MESSAGE);
            response.sendError(HttpStatus.BAD_REQUEST.value(), INVALID_AUTHORIZATION_HEADER_MESSAGE);
            return;
        }

        // Extracting token from request header
        token = requestHeader.substring(7);
        try {
            // Extracting username from the token
            username = jwtHelper.getUsernameFromToken(token);

        } catch (IllegalArgumentException e) {
            logger.error("Illegal Argument while fetching the username: {}", e);
            response.addHeader(MESSAGE, ILLEGAL_ARGUMENT_MESSAGE);
            response.sendError(HttpStatus.BAD_REQUEST.value(), ILLEGAL_ARGUMENT_MESSAGE);
            return;
        } catch (ExpiredJwtException e) {
            logger.error("Given jwt token is expired: {}", e);
            response.addHeader(MESSAGE, TOKEN_EXPIRED_MESSAGE);
            response.sendError(HttpStatus.UNAUTHORIZED.value(), TOKEN_EXPIRED_MESSAGE);
            return;
        } catch (MalformedJwtException e) {
            logger.error("Invalid Token: {}", e);
            response.addHeader(MESSAGE, INVALID_TOKEN_MESSAGE);
            response.sendError(HttpStatus.BAD_REQUEST.value(), INVALID_TOKEN_MESSAGE);
            return;
        } catch (SignatureException e) {
            logger.error("Token signature validation failed: {}", e);
            response.addHeader(MESSAGE, TOKEN_SIGNATURE_VALIDATION_FAILED_MESSAGE);
            response.sendError(HttpStatus.UNAUTHORIZED.value(), TOKEN_SIGNATURE_VALIDATION_FAILED_MESSAGE);
            return;
        } catch (Exception e) {
            logger.error("Error occurred during token validation: {}", e);
            response.addHeader(MESSAGE, INTERNAL_SEVER_ERROR_MESSAGE);
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), INTERNAL_SEVER_ERROR_MESSAGE);
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            boolean validateToken = this.jwtHelper.validateToken(token, userDetails);
            if (validateToken) {
                // Set the authentication
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                logger.error("Invalid token: {}");
                response.addHeader(MESSAGE, INVALID_TOKEN_MESSAGE);
                response.sendError(HttpStatus.BAD_REQUEST.value(), INVALID_TOKEN_MESSAGE);
                return;
            }
        }
        // Proceed with the filter chain
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return EXCLUDE_URLS.stream().anyMatch(p -> pathMatcher.match(p, request.getRequestURI()));
    }
}
