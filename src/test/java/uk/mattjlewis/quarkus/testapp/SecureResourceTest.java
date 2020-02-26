package uk.mattjlewis.quarkus.testapp;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.Test;

import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import uk.mattjlewis.quarkus.testapp.util.HttpBasicAuthUtil;

@QuarkusTest
public class SecureResourceTest {
	static final String USER_USERNAME = "user1";
	static final String USER_PASSWORD = "password";
	static final String ADMIN_USERNAME = "admin";
	static final String ADMIN_PASSWORD = "password";

	static final String ADMIN_BASIC_AUTH_TOKEN = HttpBasicAuthUtil.createHttpBasicAuthToken(ADMIN_USERNAME,
			ADMIN_PASSWORD);
	static final Object USER1_BASIC_AUTH_TOKEN = HttpBasicAuthUtil.createHttpBasicAuthToken(USER_USERNAME,
			USER_PASSWORD);

	@TestHTTPResource("rest/secured")
	URI baseUri;

	@Test
	public void securityTests() {
		Client client = ClientBuilder.newClient();
		WebTarget secured = client.target(baseUri);

		try (Response response = secured.path("permit-all").request(MediaType.TEXT_PLAIN).get()) {
			assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		}

		try (Response response = secured.path("admin").request(MediaType.TEXT_PLAIN).get()) {
			assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
		}

		try (Response response = secured.path("user").request(MediaType.TEXT_PLAIN).get()) {
			assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
		}

		given().auth().preemptive().basic(ADMIN_USERNAME, ADMIN_PASSWORD).when().basePath("rest/secured").get("admin")
				.then().statusCode(Response.Status.OK.getStatusCode());

		try (Response response = secured.path("admin").request(MediaType.TEXT_PLAIN)
				.header(HttpBasicAuthUtil.BASIC_AUTH_HEADER_NAME, ADMIN_BASIC_AUTH_TOKEN).get()) {
			assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		}

		try (Response response = secured.path("admin").request(MediaType.TEXT_PLAIN)
				.header(HttpBasicAuthUtil.BASIC_AUTH_HEADER_NAME, USER1_BASIC_AUTH_TOKEN).get()) {
			assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
		}

		try (Response response = secured.path("user").request(MediaType.TEXT_PLAIN)
				.header(HttpBasicAuthUtil.BASIC_AUTH_HEADER_NAME, ADMIN_BASIC_AUTH_TOKEN).get()) {
			assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
		}

		try (Response response = secured.path("user").request(MediaType.TEXT_PLAIN)
				.header(HttpBasicAuthUtil.BASIC_AUTH_HEADER_NAME, USER1_BASIC_AUTH_TOKEN).get()) {
			assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		}
	}
}
