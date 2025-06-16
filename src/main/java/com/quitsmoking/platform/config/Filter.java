package com.quitsmoking.platform.config;

import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.exception.exceptions.AuthenticationException;
import com.quitsmoking.platform.service.TokenService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.security.auth.message.AuthException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;

@Component
public class Filter extends OncePerRequestFilter {
    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    private final List<String> PUBLIC_API_METHOD = List.of(
            "POST:/api/register",
            "POST:/api/login"
    );


    private final List<String> PUBLIC_API = List.of(
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**"
    );




    @Autowired
    private TokenService tokenService;

    public boolean isPublicMethodAPI(String uri, String method) {
        AntPathMatcher matcher = new AntPathMatcher();

        return PUBLIC_API_METHOD.stream().anyMatch(pattern -> {
            String[] parts = pattern.split(":", 2);
            if (parts.length != 2) return false;

            String allowedMethod = parts[0];
            String allowedUri = parts[1];

            return method.equalsIgnoreCase(allowedMethod) && matcher.match(allowedUri, uri);
        });
    }

    private boolean isPermitted(String uri) {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        return PUBLIC_API.stream().anyMatch(pattern -> pathMatcher.match(pattern, uri));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String uri = request.getRequestURI();
        String method = request.getMethod();
        if (isPublicMethodAPI(uri, method) || isPermitted(uri)) {
            filterChain.doFilter(request, response);
        } else {
            String token = getToken(request);
            if (token == null) {
                resolver.resolveException(request, response, null, new AuthenticationException("Empty token!"));
                return;
            }
            Account account;
            try {
                // từ token tìm ra thằng đó là ai
                account = tokenService.extractAccount(token);


            } catch (ExpiredJwtException expiredJwtException) {
                // token het han
                resolver.resolveException(request, response, null, new AuthException("Expired Token!"));
                return;
            } catch (MalformedJwtException malformedJwtException) {
                resolver.resolveException(request, response, null, new AuthException("Invalid Token!"));
                return;
            }
            // => token dung
            UsernamePasswordAuthenticationToken
                    authenToken =
                    new UsernamePasswordAuthenticationToken(account, token, account.getAuthorities());
            authenToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenToken);

            // token ok, cho vao`
            filterChain.doFilter(request, response);
        }
    }

    public String getToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.substring(7);
    }




}
