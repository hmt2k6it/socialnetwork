package com.example.socialnetwork.module.identity.service;

import java.text.ParseException;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.socialnetwork.common.exception.AppException;
import com.example.socialnetwork.common.exception.ErrorCode;
import com.example.socialnetwork.module.identity.dto.request.AuthenticationRequest;
import com.example.socialnetwork.module.identity.dto.request.IntrospectRequest;
import com.example.socialnetwork.module.identity.dto.response.AuthenticationResponse;
import com.example.socialnetwork.module.identity.dto.response.IntrospectResponse;
import com.example.socialnetwork.module.identity.entity.User;
import com.example.socialnetwork.module.identity.repository.UserRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationService {
    @NonFinal
    @Value("${jwt.secret}")
    String secretKey;
    @NonFinal
    @Value("${jwt.access-token.expiration-time}")
    long accessTokenExpirationTime;
    @NonFinal
    @Value("${jwt.refresh-token.expiration-time}")
    long refreshTokenExpirationTime;

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        User user = userRepository.findByUsername(authenticationRequest.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (!passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_INCORRECT);
        }
        String accessToken = generateToken(user, false);
        String refreshToken = generateToken(user, true);
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private String generateToken(User user, boolean isRefreshToken) {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS256);
        JWTClaimsSet.Builder jwtClaimsSetBuilder = new JWTClaimsSet.Builder()
                .subject(user.getUserId())
                .claim("username", user.getUsername())
                .issuer("socialnetwork.com")
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis()
                        + (isRefreshToken ? refreshTokenExpirationTime : accessTokenExpirationTime)));

        if (!isRefreshToken) {
            jwtClaimsSetBuilder.claim("scope", buildScope(user));
        }

        JWTClaimsSet jwtClaimsSet = jwtClaimsSetBuilder.build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);
        try {
            jwsObject.sign(new MACSigner(secretKey.getBytes()));
            return jwsObject.serialize();
        } catch (Exception e) {
            log.error("Error generating token", e);
            throw new RuntimeException(e);
        }
    }

    public IntrospectResponse introspect(IntrospectRequest request) {
        boolean isValid = verifyToken(request.getAccessToken());
        return IntrospectResponse.builder()
                .isValid(isValid)
                .build();
    }

    private boolean verifyToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier jwsVerifier = new MACVerifier(secretKey.getBytes());
            Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            if (expirationTime.before(new Date()) || !signedJWT.verify(jwsVerifier)) {
                return false;
            }
            return true;
        } catch (ParseException | JOSEException e) {
            return false;
        }
    }

    private String buildScope(User user) {
        return user.getRoles().stream().map(role -> "ROLE_" + role.getName()).collect(Collectors.joining(" "));
    }

}
