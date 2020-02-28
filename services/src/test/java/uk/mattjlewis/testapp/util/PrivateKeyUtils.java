package uk.mattjlewis.testapp.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class PrivateKeyUtils {
	private PrivateKeyUtils() {
		// no-op: utility class
	}

	/**
	 * Read a PEM encoded private key from the classpath
	 *
	 * @param pemResName - key file resource name
	 * @return PrivateKey
	 * @throws GeneralSecurityException If there is an error parsing the private key
	 * @throws IOException
	 */
	public static PrivateKey readPrivateKey(final String pemResourceName) throws GeneralSecurityException, IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int buffer_length = 4096;
		byte[] buffer = new byte[buffer_length];
		int read;

		try (InputStream is = TokenUtils.class.getResourceAsStream(pemResourceName)) {
			while ((read = is.read(buffer)) != -1) {
				baos.write(buffer, 0, read);
			}
		}

		return decodePrivateKey(new String(baos.toByteArray(), StandardCharsets.UTF_8));
	}

	/**
	 * Decode a PEM encoded private key string to an RSA PrivateKey
	 *
	 * @param pemEncoded - PEM string for private key
	 * @return PrivateKey
	 * @throws GeneralSecurityException If there is an error parsing the private key
	 */
	public static PrivateKey decodePrivateKey(final String pemEncoded) throws GeneralSecurityException {
		return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(toEncodedBytes(pemEncoded)));
	}

	private static byte[] toEncodedBytes(final String pemEncoded) {
		return Base64.getDecoder().decode(removeBeginEnd(pemEncoded));
	}

	private static String removeBeginEnd(String pem) {
		String cleansed_pem = pem.replaceAll("-----BEGIN (.*)-----", "");
		cleansed_pem = cleansed_pem.replaceAll("-----END (.*)----", "");
		cleansed_pem = cleansed_pem.replaceAll("\r\n", "");
		cleansed_pem = cleansed_pem.replaceAll("\n", "");
		return cleansed_pem.trim();
	}
}
