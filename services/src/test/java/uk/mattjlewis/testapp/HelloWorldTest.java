package uk.mattjlewis.testapp;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import javax.ws.rs.core.Response;

@QuarkusTest
@SuppressWarnings("static-method")
public class HelloWorldTest {
	@Test
	public void testHelloEndpoint() {
		given().when().basePath("rest").get("hello").then().statusCode(Response.Status.OK.getStatusCode())
				.body(is("Hello world!"));
	}
}
