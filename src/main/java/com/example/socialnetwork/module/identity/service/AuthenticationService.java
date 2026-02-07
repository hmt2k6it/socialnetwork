package com.example.socialnetwork.module.identity.service;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.socialnetwork.module.identity.dto.request.UserCreationRequest;
import com.example.socialnetwork.module.identity.entity.RefreshToken;
import com.example.socialnetwork.module.identity.entity.Role;
import com.example.socialnetwork.module.identity.mapper.UserMapper;
import com.example.socialnetwork.module.identity.repository.RefreshTokenRepository;
import com.example.socialnetwork.module.identity.repository.RoleRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.socialnetwork.common.exception.AppException;
import com.example.socialnetwork.common.exception.ErrorCode;
import com.example.socialnetwork.module.identity.dto.request.AuthenticationRequest;
import com.example.socialnetwork.module.identity.dto.request.ForgetPasswordRequest;
import com.example.socialnetwork.module.identity.dto.request.LogoutRequest;
import com.example.socialnetwork.module.identity.dto.request.RefreshTokenRequest;
import com.example.socialnetwork.module.identity.dto.request.ResetPasswordRequest;
import com.example.socialnetwork.module.identity.dto.response.AuthenticationResponse;
import com.example.socialnetwork.module.identity.entity.User;
import com.example.socialnetwork.module.identity.repository.UserRepository;
import com.github.benmanes.caffeine.cache.Cache;
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
    @Value("${jwt.signerKey}")
    String signerKey;
    @NonFinal
    @Value("${jwt.access-token.expiration-time}")
    long accessTokenExpirationTime;
    @NonFinal
    @Value("${jwt.refresh-token.expiration-time}")
    long refreshTokenExpirationTime;

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;
    UserMapper userMapper;
    RefreshTokenRepository refreshTokenRepository;
    CacheManager cacheManager;
    Cache<String, String> otpCache;

    @Transactional
    public AuthenticationResponse register(UserCreationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USERNAME_EXIST);
        }
        Role role = roleRepository.findById("USER").orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Set.of(role));
        user = userRepository.save(user);

        return generateAuthenticationResponse(user, true);
    }

    @Transactional
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        User user = userRepository.findByUsername(authenticationRequest.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (!passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_INCORRECT);
        }
        return generateAuthenticationResponse(user, true);
    }

    @Transactional
    public void logout(LogoutRequest request) {
        String accessToken = request.getAccessToken();
        String refreshToken = request.getRefreshToken();
        try {
            SignedJWT refreshTokenSignedJWT = SignedJWT.parse(refreshToken);
            String refreshTokenJti = refreshTokenSignedJWT.getJWTClaimsSet().getJWTID();
            refreshTokenRepository.deleteById(refreshTokenJti);

            SignedJWT accessTokenSignedJWT = SignedJWT.parse(accessToken);
            String accessTokenJti = accessTokenSignedJWT.getJWTClaimsSet().getJWTID();
            if (cacheManager.getCache("invalidated_tokens") != null) {
                cacheManager.getCache("invalidated_tokens").put(accessTokenJti, accessTokenJti);
            }
            log.info("Logout successfully");
        } catch (Exception e) {
            log.error("Error when logout", e);
        }
    }

    @Transactional
    public AuthenticationResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        try {
            SignedJWT refreshTokenSignedJWT = SignedJWT.parse(refreshToken);
            JWSVerifier jwsVerifier = new MACVerifier(signerKey.getBytes());
            if (!refreshTokenSignedJWT.verify(jwsVerifier)) {
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }
            String refreshTokenJti = refreshTokenSignedJWT.getJWTClaimsSet().getJWTID();
            RefreshToken refreshTokenEntity = refreshTokenRepository.findById(refreshTokenJti)
                    .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
            if (refreshTokenEntity.getExpiryDate().before(new Date())) {
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }
            refreshTokenRepository.deleteById(refreshTokenJti);
            User user = refreshTokenEntity.getUser();
            return generateAuthenticationResponse(user, false);
        } catch (Exception e) {
            log.error("Error when refresh token", e);
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
    }

    public String forgetPassword(ForgetPasswordRequest request) {
        String email = request.getEmail();
        if (userRepository.existsByEmail(email)) {
            String otp = generateOtp();
            otpCache.put(email, otp);
            // TODO: Send OTP to email via EmailService
        }
        return "If user exists, OTP has been sent to your email";
    }

    @Transactional
    public String resetPassword(ResetPasswordRequest request) {
        String email = request.getEmail();
        String otp = request.getOtp();
        var cache = otpCache.getIfPresent(email);
        if (cache == null || !cache.equals(otp)) {
            throw new AppException(ErrorCode.INVALID_OTP);
        }
        otpCache.invalidate(email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        return "Password reset successfully";
    }

    private AuthenticationResponse generateAuthenticationResponse(User user, boolean hasUser) {
        String accessJti = UUID.randomUUID().toString();
        Date accessTokenExpiryDate = new Date(System.currentTimeMillis() + accessTokenExpirationTime * 1000);
        String accessToken = generateToken(user, false, accessJti, accessTokenExpiryDate);

        String refreshJti = UUID.randomUUID().toString();
        Date refreshExpiryDate = new Date(System.currentTimeMillis() + refreshTokenExpirationTime * 1000);
        String refreshToken = generateToken(user, true, refreshJti, refreshExpiryDate);

        refreshTokenRepository.save(RefreshToken.builder()
                .jti(refreshJti)
                .user(user)
                .expiryDate(refreshExpiryDate)
                .build());

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(hasUser ? userMapper.toUserResponse(user) : null)
                .build();
    }

    private String generateToken(User user, boolean isRefreshToken, String jti, Date expiryDate) {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS256);
        JWTClaimsSet.Builder jwtClaimsSetBuilder = new JWTClaimsSet.Builder()
                .jwtID(jti)
                .subject(user.getUserId())
                .claim("username", user.getUsername())
                .issuer("socialnetwork.com")
                .issueTime(new Date())
                .expirationTime(expiryDate);

        if (!isRefreshToken) {
            jwtClaimsSetBuilder.claim("scope", buildScope(user));
        }

        JWTClaimsSet jwtClaimsSet = jwtClaimsSetBuilder.build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);
        try {
            jwsObject.sign(new MACSigner(signerKey.getBytes()));
            return jwsObject.serialize();
        } catch (Exception e) {
            log.error("Error generating token", e);
            throw new RuntimeException(e);
        }
    }

    private String buildScope(User user) {
        return user.getRoles().stream().map(role -> "ROLE_" + role.getName()).collect(Collectors.joining(" "));
    }

    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        int number = random.nextInt(1000000);
        return String.format("%06d", number);
    }
}