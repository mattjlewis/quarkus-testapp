package uk.mattjlewis.quarkus.testapp;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import uk.mattjlewis.quarkus.testapp.model.Department;
import uk.mattjlewis.quarkus.testapp.model.Employee;

@QuarkusTest
@SuppressWarnings("static-method")
public class RestAssuredDepartmentResourceTest {
	private static final String DEPARTMENT_PATH = "department";

	@Test
	public void restClientDepartmentTest() {
		// Create a department with employees
		List<Employee> employees = Arrays.asList(new Employee("Matt", "matt@test.org", "Coffee"),
				new Employee("Fred", "fred@test.org", "Beer"));
		Department dept = new Department("IT", "London", employees);

		Response response = given().body(dept).contentType(MediaType.APPLICATION_JSON).when().basePath("rest").post(DEPARTMENT_PATH);
		response.then().statusCode(201);
		String location = response.header("Location");
		assertNotNull(location);
		Department created_dept = response.as(Department.class);
		assertNotNull(created_dept);
		assertNotNull(created_dept.getId());
		assertEquals(dept.getName(), created_dept.getName());
		assertEquals(dept.getLocation(), created_dept.getLocation());
		
		response = given().get(location);
		response.then().statusCode(200);
		Department found_dept = response.as(Department.class);
		assertNotNull(found_dept);
		assertNotNull(found_dept.getId());
		assertEquals(dept.getName(), found_dept.getName());
		assertEquals(employees.size(), found_dept.getEmployees().size());
		assertEquals(dept.getEmployees().size(), found_dept.getEmployees().size());
		assertEquals(0, found_dept.getVersion().intValue());
		
		var base_request = given().pathParam("deptId", created_dept.getId()).accept(MediaType.APPLICATION_JSON).when().basePath("rest");
		
		// Find the department
		response = base_request.get(DEPARTMENT_PATH + "/{deptId}");
		response.then().statusCode(200);
		found_dept = response.as(Department.class);
		assertNotNull(found_dept);
		assertNotNull(found_dept.getId());
		assertEquals(dept.getName(), found_dept.getName());
		assertEquals(employees.size(), found_dept.getEmployees().size());
		assertEquals(dept.getEmployees().size(), found_dept.getEmployees().size());
		assertEquals(0, found_dept.getVersion().intValue());

		/*
		// Update the department
		found_dept.setName(dept.getName() + " - updated");
		given().pathParam("deptId", created_dept.getId()).contentType(MediaType.APPLICATION_JSON).when().basePath("rest").patch(DEPARTMENT_PATH + "/{deptId}");
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
		*/
	}
}
