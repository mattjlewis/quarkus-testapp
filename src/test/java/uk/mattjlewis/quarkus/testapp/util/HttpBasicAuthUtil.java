package uk.mattjlewis.quarkus.testapp.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class HttpBasicAuthUtil {
	public static final String BASIC_AUTH_HEADER_NAME = "Authorization";
	private static final String BASIC_AUTH_NAME = "Basic";

	private HttpBasicAuthUtil() {
		// no-op: utility class
	}

	public static String createHttpBasicAuthToken(String username, String password) {
		return BASIC_AUTH_NAME + " "
				+ Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
	}
}
