package uk.mattjlewis.testapp;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.path.json.mapper.factory.DefaultJackson2ObjectMapperFactory;
import io.restassured.response.Response;
import uk.mattjlewis.testapp.model.Department;
import uk.mattjlewis.testapp.model.Employee;

@QuarkusTest
@SuppressWarnings("static-method")
public class RestAssuredDepartmentResourceTest {
	private static final String DEPARTMENT_PATH = "department";

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
		System.out
				.println("Deleted " + entityManager.createQuery("DELETE FROM Employee").executeUpdate() + " Employees");
		System.out.println(
				"Deleted " + entityManager.createQuery("DELETE FROM Department").executeUpdate() + " Departments");
	}

	@Test
	public void restClientDepartmentTest() {
		RestAssured.config = RestAssuredConfig.config().objectMapperConfig(ObjectMapperConfig.objectMapperConfig()
				.jackson2ObjectMapperFactory(new DefaultJackson2ObjectMapperFactory() {
					@Override
					public ObjectMapper create(Type cls, String charset) {
						ObjectMapper om = super.create(cls, charset);
						return om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
					}
				}));

		// Create a department with employees
		List<Employee> employees = Arrays.asList(new Employee("Matt", "matt@test.org", "Coffee"),
				new Employee("Fred", "fred@test.org", "Beer"));
		Department dept = new Department("IT", "London", employees);

		Response response = given().body(dept).contentType(MediaType.APPLICATION_JSON).when().basePath("rest")
				.post(DEPARTMENT_PATH);
		response.then().statusCode(HttpURLConnection.HTTP_CREATED);
		String location = response.header("Location");
		assertNotNull(location);
		Department created_dept = response.as(Department.class);
		assertNotNull(created_dept);
		assertNotNull(created_dept.getId());
		assertEquals(dept.getName(), created_dept.getName());
		assertEquals(dept.getLocation(), created_dept.getLocation());
		System.out.println("Created: " + created_dept.getCreated());
		System.out.println("Last Updated: " + created_dept.getCreated());

		response = given().get(location);
		response.then().statusCode(HttpURLConnection.HTTP_OK);
		Department found_dept = response.as(Department.class);
		assertNotNull(found_dept);
		assertNotNull(found_dept.getId());
		assertEquals(dept.getName(), found_dept.getName());
		assertEquals(employees.size(), found_dept.getEmployees().size());
		assertEquals(dept.getEmployees().size(), found_dept.getEmployees().size());
		assertEquals(0, found_dept.getVersion().intValue());

		// Find the department
		response = given().pathParam("deptId", created_dept.getId()).accept(MediaType.APPLICATION_JSON).when()
				.basePath("rest").get(DEPARTMENT_PATH + "/{deptId}");
		response.then().statusCode(HttpURLConnection.HTTP_OK);
		found_dept = response.as(Department.class);
		assertNotNull(found_dept);
		assertNotNull(found_dept.getId());
		assertEquals(dept.getName(), found_dept.getName());
		assertEquals(employees.size(), found_dept.getEmployees().size());
		assertEquals(dept.getEmployees().size(), found_dept.getEmployees().size());
		assertEquals(0, found_dept.getVersion().intValue());

		// Update the department
		found_dept.setName(dept.getName() + " - updated");
		System.out.println("Calling update...");
		response = given().body(found_dept)
				.contentType(MediaType.APPLICATION_JSON).when().basePath("rest").put(DEPARTMENT_PATH);
		System.out.println("Called update.");
		response.then().statusCode(HttpURLConnection.HTTP_OK);
		System.out.println("After status check");
		Department updated_dept = response.as(Department.class);
		assertNotNull(updated_dept);
		assertEquals(dept.getName() + " - updated", updated_dept.getName());
		assertEquals(found_dept.getVersion().intValue() + 1, updated_dept.getVersion().intValue());

		// Should trigger bean validation failure
		dept = new Department("012345678901234567890123456789", "London");
		given().body(dept).contentType(MediaType.APPLICATION_JSON).when().basePath("rest").post(DEPARTMENT_PATH).then()
				.statusCode(HttpURLConnection.HTTP_BAD_REQUEST);

		// Should pass bean validation but trigger database constraint violation
		dept = new Department("HR", "Reading",
				Arrays.asList(new Employee("Rod", "rod@test.org", "Water"),
						new Employee("Jane", "jane@test.org", "012345678901234567890123456789"),
						new Employee("Freddie", "freddie@test.org", "Tea")));
		given().body(dept).contentType(MediaType.APPLICATION_JSON).when().basePath("rest").post(DEPARTMENT_PATH).then()
				.statusCode(HttpURLConnection.HTTP_CONFLICT);
	}
}
