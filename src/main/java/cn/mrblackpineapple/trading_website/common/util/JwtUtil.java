package cn.mrblackpineapple.trading_website.common.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

public class JwtUtil {

    private static final String privateKeyStr = "d8c986df-8512-42b5-906f-eeea9b3acf86";

    private static final Key SIGNING_KEY = Keys.hmacShaKeyFor(privateKeyStr.getBytes(StandardCharsets.UTF_8));

    private static final Integer time = 1000 * 60 * 60 * 24 * 7;

    public static String toToken(Integer id, Integer role) {
        JwtBuilder jwtBuilder = Jwts.builder();
        return jwtBuilder.setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                .claim("id", id)
                .claim("role", role)
                .setSubject("user authentification")
                .setExpiration(new Date(System.currentTimeMillis() + time))
                .setId(UUID.randomUUID().toString())
                .signWith(SIGNING_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public static Claims fromToken(String token) {
        JwtParser jwtParser = Jwts.parser();
        Jws<Claims> claimsJws;
        try {
            claimsJws = jwtParser.setSigningKey(SIGNING_KEY).parseClaimsJws(token);
            return claimsJws.getBody();
        } catch (Exception e) {
            return null;
        }
    }

}
