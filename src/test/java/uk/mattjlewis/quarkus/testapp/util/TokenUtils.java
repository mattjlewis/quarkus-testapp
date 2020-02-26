package uk.mattjlewis.quarkus.testapp.util;

import java.security.PrivateKey;

import org.eclipse.microprofile.jwt.Claims;

import io.smallrye.jwt.build.Jwt;
import io.smallrye.jwt.build.JwtClaimsBuilder;

/**
 * Utilities for generating a JWT for testing
 */
public class TokenUtils {
	private TokenUtils() {
		// no-op: utility class
	}

	public static String generateTokenString(PrivateKey privateKey, String keyId, String claimsJson,
			long durationSecs) {
		long current_time_in_secs = currentTimeInSecs();

		long expiry_time_secs = current_time_in_secs + durationSecs;

		JwtClaimsBuilder claims = Jwt.claims(claimsJson).issuedAt(current_time_in_secs)
				.claim(Claims.auth_time.name(), Long.valueOf(current_time_in_secs))
				.claim(Claims.nbf.name(), Long.valueOf(current_time_in_secs)).expiresAt(expiry_time_secs);

		return claims.jws().signatureKeyId(keyId).sign(privateKey);
	}

	/**
	 * @return the current time in seconds since epoch
	 */
	public static int currentTimeInSecs() {
		return (int) (System.currentTimeMillis() / 1000);
	}
}
