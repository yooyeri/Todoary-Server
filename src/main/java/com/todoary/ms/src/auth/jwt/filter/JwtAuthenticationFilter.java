package com.todoary.ms.src.auth.jwt.filter;

import com.todoary.ms.src.auth.AuthService;
import com.todoary.ms.src.auth.dto.PostSigninRes;
import com.todoary.ms.src.auth.jwt.JwtTokenProvider;
import com.todoary.ms.src.auth.model.PrincipalDetails;
import com.todoary.ms.src.auth.model.Token;
import com.todoary.ms.src.user.model.User;
import com.todoary.ms.util.BaseResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final AuthService authService;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder, AuthService authService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.authService = authService;
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        Authentication authentication = null;
        try {

            ObjectMapper om = new ObjectMapper();
            User user = om.readValue(request.getInputStream(), User.class); //json으로 넘어온 username과 password user에 담아줌.
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
                        // 여기서 PrincipalDetailsService의 loadByUsername 실행됨
            authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            // ^ 갔다와서 실행됨
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return authentication;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();

        Long userid = principalDetails.getUser().getId();
        String accessToken = jwtTokenProvider.createAccessToken(userid);
        String refreshToken = jwtTokenProvider.createRefreshToken(userid);

        authService.registerRefreshToken(userid, refreshToken);

        Token token = new Token(accessToken, refreshToken);
        PostSigninRes postSigninRes = new PostSigninRes(token);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), new BaseResponse<PostSigninRes>(postSigninRes));

    }
}
