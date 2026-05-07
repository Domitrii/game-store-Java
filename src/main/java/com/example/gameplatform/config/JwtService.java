package com.example.gameplatform.config;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-seconds:86400}")
    private long expirationSeconds;

    public String generateToken(String userId, String email) throws JOSEException {
        JWSSigner signer = new MACSigner(secret.getBytes(StandardCharsets.UTF_8));

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(userId)
                .claim("email", email)
                .issuer("game-platform")
                .issueTime(Date.from(Instant.now()))
                .expirationTime(Date.from(Instant.now().plusSeconds(expirationSeconds)))
                .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
        signedJWT.sign(signer);
        return signedJWT.serialize();
    }
}
