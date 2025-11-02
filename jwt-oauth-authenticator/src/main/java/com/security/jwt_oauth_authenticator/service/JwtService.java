package com.security.jwt_oauth_authenticator.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Service
public class JwtService {

    private final Key signingKey;

    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtService(
          @Value("${app.jwt.secret}") String jwtSecretString,
          @Value("${app.jwt.access-token-expiration-ms}") long accessTokenExpiration,
          @Value("${app.jwt.refresh-token-expiration-ms}") long refreshTokenExpiration
    ){

        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;

        // 1. Decode the Base64 secret string
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecretString);

        // 2. Create a secure Key object from the bytes
        // This key will be used to sign and verify all tokens
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);

    }

    // --- Token Generation Methods ---

    /**
     * Generates a short-lived Access Token for a user.
     */

    public String generateAccessToken(UserDetails userDetails){

        // We can add extra claims (like roles) here if we want
        Map<String, Object> extraClaims =new HashMap<>();
        return buildToken(extraClaims,userDetails,accessTokenExpiration);
    }

/**
 * Generates a long-lived Refresh Token for a user.
 * Note: It contains no extra claims, only the user's subject (email).
 */
    public String generateRefreshToken(UserDetails userDetails){
        return buildToken(new HashMap<>(),userDetails,refreshTokenExpiration);
    }


    /**
     * The main private helper method to build a token.
     */
    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expirationMs
    ){
        long currentTime = System.currentTimeMillis();

        return Jwts.builder()
                .claims(extraClaims) // Add any extra claims
                .subject(userDetails.getUsername())  // Set the "subject" (who the token is for)
                .issuedAt(new Date(currentTime))// Set the "issued at" time
                .expiration(new Date(currentTime + expirationMs)) // Set the "expiration" time
                .signWith(signingKey) // Sign the token with our secure key
                .compact();  // Build the token into a compact string

    }

    // --- Token Validation and Claim Extraction Methods ---
    /**
     * Checks if a token is valid (i.e., belongs to the user and is not expired).
     */
    public boolean isTokenValid(String token, UserDetails userDetails){
        try{
            final String username= extractUsername(token);
            return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);

        } catch (Exception e) {
            // Catches any parsing/validation errors (e.g., expired, malformed)
            return false;
        }
    }

    /**
     * Extracts the username (email) from the token's "subject" claim.
     */
    public String extractUsername(String token){
        return extractClaim(token,Claims::getSubject);
    }

    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token){
        return extractClaim(token,Claims::getExpiration);
    }

    /**
     * Generic helper to extract a specific claim from the token.
     */
    public <T> T extractClaim(String token, Function<Claims,T> claimsResolver){
        final Claims claims =extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * The core parsing method.
     * This method parses the token string and validates its signature.
     * If the signature is invalid or the token is malformed, it throws an exception.
     */
    public Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith((SecretKey) signingKey ) // Use our key to verify the signature
                .build()
                .parseSignedClaims(token)
                .getPayload(); // Get the "body" (payload) of the token
    }

}
