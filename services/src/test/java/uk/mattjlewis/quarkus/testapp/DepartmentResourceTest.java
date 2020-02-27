package uk.mattjlewis.quarkus.testapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import uk.mattjlewis.quarkus.testapp.model.Department;
import uk.mattjlewis.quarkus.testapp.model.Employee;

@QuarkusTest
public class DepartmentResourceTest {
	private static final String DEPARTMENT_PATH = "department";
	
	@TestHTTPResource("rest")
	URI baseUri;

	@PersistenceContext
	EntityManager entityManager;

	@BeforeEach
	public void cleanupBefore() {
		cleanup();
	}
	
	@AfterEach
	public void cleanupAfter() {
		cleanup();
	}
	
	@Transactional
	public void cleanup() {
		System.out.println("Deleted " + entityManager.createQuery("DELETE FROM Employee").executeUpdate() + " Employees");
		System.out.println("Deleted " + entityManager.createQuery("DELETE FROM Department").executeUpdate() + " Departments");
	}

	@Test
	public void restClientDepartmentTest() {
		Client client = ClientBuilder.newClient();
		// Required to use PATCH when using the Jersey REST client
		client.property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, Boolean.TRUE);
		WebTarget root = client.target(baseUri);

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
			Department updated_dept = root.path(DEPARTMENT_PATH).path(found_dept.getId().toString())
					.request(MediaType.APPLICATION_JSON)
					.method(HttpMethod.PATCH, Entity.json(found_dept), Department.class);
			assertNotNull(updated_dept);
			assertEquals(created_dept.getName() + " - updated", updated_dept.getName());
			assertEquals(created_dept.getVersion().intValue() + 1, updated_dept.getVersion().intValue());
			System.out.println("Version after update #1: " + updated_dept.getVersion());
			found_dept.setVersion(updated_dept.getVersion());
		} catch (WebApplicationException e) {
			fail("Unexpected response status: " + e.getResponse().getStatus());
		}

		// Fetch the department to validate it was updated
		try {
			found_dept = root.path(DEPARTMENT_PATH).path(created_dept.getId().toString())
					.request(MediaType.APPLICATION_JSON).get(Department.class);
			assertNotNull(found_dept);
			assertNotNull(found_dept.getId());
			assertEquals(created_dept.getName() + " - updated", found_dept.getName());
			assertEquals(employees.size(), found_dept.getEmployees().size());
			assertEquals(created_dept.getEmployees().size(), found_dept.getEmployees().size());
			assertEquals(created_dept.getVersion().intValue() + 1, found_dept.getVersion().intValue());
		} catch (WebApplicationException e) {
			fail("Unexpected response status: " + e.getResponse().getStatus());
			// Here simply to avoid the compiler warning about a potential null pointer access
			return;
		}

		// Update the department again
		found_dept.setName(dept.getName());
		try {
			Department updated_dept = root.path(DEPARTMENT_PATH).path(found_dept.getId().toString())
					.request(MediaType.APPLICATION_JSON)
					.method(HttpMethod.PATCH, Entity.json(found_dept), Department.class);
			assertNotNull(updated_dept);
			assertEquals(created_dept.getName(), updated_dept.getName());
			assertEquals(created_dept.getVersion().intValue() + 2, updated_dept.getVersion().intValue());
			System.out.println("Version after update #2: " + updated_dept.getVersion());
		} catch (WebApplicationException e) {
			fail("Unexpected response status: " + e.getResponse().getStatus());
		}

		// Make sure the department was updated
		try {
			Department updated_dept = root.path(DEPARTMENT_PATH).path(created_dept.getId().toString())
					.request(MediaType.APPLICATION_JSON).get(Department.class);
			assertNotNull(updated_dept);
			assertEquals(created_dept.getName(), updated_dept.getName());
			assertEquals(created_dept.getVersion().intValue() + 2, updated_dept.getVersion().intValue());
		} catch (WebApplicationException e) {
			fail("Unexpected response status: " + e.getResponse().getStatus());
			// Here simply to avoid the compiler warning about a potential null pointer access
			return;
		}
	}
	
	public void validationErrors() {
		Client client = ClientBuilder.newClient();
		// Required to use PATCH when using the Jersey REST client
		client.property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, Boolean.TRUE);
		WebTarget root = client.target(baseUri);

		// Should trigger bean validation failure
		Department dept = new Department("012345678901234567890123456789", "London");
		try (Response response = root.path(DEPARTMENT_PATH).request(MediaType.APPLICATION_JSON)
				.post(Entity.entity(dept, MediaType.APPLICATION_JSON))) {
			assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
		} catch (ProcessingException e) {
			// Ignore
		}

		// Should pass bean validation but trigger database constraint violation due to a deliberate
		// mismatch between validation rules and database constraint on Employee favourite drink
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
