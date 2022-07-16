package com.todoary.ms.src.auth;

import com.todoary.ms.src.auth.jwt.JwtTokenProvider;
import com.todoary.ms.src.auth.model.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthDao authDao;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public AuthService(AuthDao authDao, JwtTokenProvider jwtTokenProvider) {
        this.authDao = authDao;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public Long createRefreshToken(Long userid, String refreshToken) {
        if(authDao.checkUser(userid))
            authDao.updateRefreshToken(userid, refreshToken);
        else
            authDao.insertRefreshToken(userid,refreshToken);

        return userid;
    }

//    public Long modifyRefreshToken(Long userid, String refreshToken) {
//        this.authDao.updateRefreshToken(userid,refreshToken);
//
//        return userid;
//    }

    public Token createAccess(String refreshToken) {

        Long userid = Long.parseLong(jwtTokenProvider.getUseridFromRef(refreshToken));

        String newAccessToken = jwtTokenProvider.createAccessToken(userid);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(userid);

        authDao.updateRefreshToken(userid, newRefreshToken);

        Token newTokens = new Token(newAccessToken, newRefreshToken);
        return newTokens;
    }

}
