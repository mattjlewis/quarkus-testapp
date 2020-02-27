package uk.mattjlewis.quarkus.testapp;

import java.security.PrivateKey;

import uk.mattjlewis.quarkus.testapp.util.PrivateKeyUtils;
import uk.mattjlewis.quarkus.testapp.util.TokenUtils;

/**
 * A simple utility class to generate and print a JWT token string to stdout. Can be run with: mvn exec:java
 * -Dexec.mainClass=uk.mattjlewis.quarkus.testapp.GenerateJwt -Dexec.classpathScope=test
 */
public class GenerateJwt {
	/**
	 * @param args - [0]: optional name of classpath resource for json document of claims to add; defaults to
	 *             "/JwtClaims.json" [1]: optional time in seconds for expiration of generated token; defaults to 300
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		String claimsJson = "/JwtClaims.json";
		if (args.length > 0) {
			claimsJson = args[0];
		}

		long duration;
		if (args.length > 1) {
			duration = Long.parseLong(args[1]);
		} else {
			duration = 3600;
		}

		String private_key_name = "/privateKey.pem";
		PrivateKey pk = PrivateKeyUtils.readPrivateKey(private_key_name);

		String token = TokenUtils.generateTokenString(pk, private_key_name, claimsJson, duration);
		System.out.println(token);
	}
}
