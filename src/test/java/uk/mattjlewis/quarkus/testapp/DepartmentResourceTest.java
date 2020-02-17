package uk.mattjlewis.quarkus.testapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import uk.mattjlewis.quarkus.testapp.model.Department;
import uk.mattjlewis.quarkus.testapp.model.Employee;

@QuarkusTest
@SuppressWarnings("static-method")
public class DepartmentResourceTest {
	private static final String DEPARTMENT_PATH = "department";

	@Test
	public void restClientDepartmentTest() {
		Client client = ClientBuilder.newClient();
		// Required to use PATCH when using the Jersey REST client
		client.property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, Boolean.TRUE);
		WebTarget root = client.target("http://localhost:8081").path("rest");

		// Create a department with employees
		List<Employee> employees = Arrays.asList(new Employee("Matt", "matt@test.org", "Coffee"),
				new Employee("Fred", "fred@test.org", "Beer"));
		Department dept = new Department("IT", "London", employees);

		Department created_dept;
		URI location;
		try (Response response = root.path(DEPARTMENT_PATH).request(MediaType.APPLICATION_JSON)
				.post(Entity.json(dept))) {
			assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
			location = response.getLocation();
			assertNotNull(location);
			System.out.println("Location response header: " + response.getHeaderString("Location"));
			created_dept = response.readEntity(Department.class);
			assertNotNull(created_dept);
			assertNotNull(created_dept.getId());
			assertEquals(dept.getName(), created_dept.getName());
			assertEquals(dept.getLocation(), created_dept.getLocation());
		}

		// Find the department
		Department found_dept;
		try {
			found_dept = root.path(DEPARTMENT_PATH).path(created_dept.getId().toString())
					.request(MediaType.APPLICATION_JSON).get(Department.class);
			assertNotNull(found_dept);
			assertNotNull(found_dept.getId());
			assertEquals(dept.getName(), found_dept.getName());
			assertEquals(employees.size(), found_dept.getEmployees().size());
			assertEquals(dept.getEmployees().size(), found_dept.getEmployees().size());
			assertEquals(0, found_dept.getVersion().intValue());
		} catch (WebApplicationException e) {
			fail("Unexpected response status: " + e.getResponse().getStatus());
			// Here simply to avoid the compiler warning about a potential null pointer access
			return;
		}

		// Use the returned location to find the department
		try {
			found_dept = client.target(location).request(MediaType.APPLICATION_JSON).get(Department.class);
			assertNotNull(found_dept);
			assertNotNull(found_dept.getId());
			assertEquals(dept.getName(), found_dept.getName());
			assertEquals(employees.size(), found_dept.getEmployees().size());
			assertEquals(dept.getEmployees().size(), found_dept.getEmployees().size());
			assertEquals(0, found_dept.getVersion().intValue());
		} catch (WebApplicationException e) {
			fail("Unexpected response status: " + e.getResponse().getStatus());
			// Here simply to avoid the compiler warning about a potential null pointer access
			return;
		}

		// Update the department
		found_dept.setName(dept.getName() + " - updated");
		try {
			Department updated_dept = root.path(DEPARTMENT_PATH).path(created_dept.getId().toString())
					.request(MediaType.APPLICATION_JSON)
					.method(HttpMethod.PATCH, Entity.json(found_dept), Department.class);
			assertNotNull(updated_dept);
			assertEquals(dept.getName() + " - updated", updated_dept.getName());
			assertEquals(found_dept.getVersion().intValue() + 1, updated_dept.getVersion().intValue());
		} catch (WebApplicationException e) {
			fail("Unexpected response status: " + e.getResponse().getStatus());
		}

		// Should trigger bean validation failure
		dept = new Department("012345678901234567890123456789", "London");
		try (Response response = root.path(DEPARTMENT_PATH).request(MediaType.APPLICATION_JSON)
				.post(Entity.entity(dept, MediaType.APPLICATION_JSON))) {
			assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
		} catch (ProcessingException e) {
			// Ignore
		}

		// Should pass bean validation but trigger database constraint violation
		dept = new Department("HR", "Reading",
				Arrays.asList(new Employee("Rod", "rod@test.org", "Water"),
						new Employee("Jane", "jane@test.org", "012345678901234567890123456789"),
						new Employee("Freddie", "freddie@test.org", "Tea")));
		try (Response response = root.path(DEPARTMENT_PATH).request(MediaType.APPLICATION_JSON)
				.post(Entity.entity(dept, MediaType.APPLICATION_JSON))) {
			assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
		}
	}
}
