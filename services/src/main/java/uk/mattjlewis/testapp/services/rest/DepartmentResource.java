package uk.mattjlewis.testapp.services.rest;

import java.net.URI;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import uk.mattjlewis.testapp.model.Department;
import uk.mattjlewis.testapp.model.Employee;
import uk.mattjlewis.testapp.services.service.DepartmentServiceInterface;

@Path("department")
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class DepartmentResource {
	@Inject
	DepartmentServiceInterface departmentService;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(summary = "Create a new department")
	@APIResponse(responseCode = "201",
			description = "The created department",
			content = @Content(mediaType = MediaType.APPLICATION_JSON,
					schema = @Schema(type = SchemaType.OBJECT, implementation = Department.class)))
	// FIXME I don't think PermitAll should be required here
	@PermitAll
	public Response create(@Context UriInfo uriInfo,
			@Valid @Parameter(schema = @Schema(implementation = Department.class)) Department department) {
		System.out.format(">>> create(%s)%n", department.getName());

		testValidateDepartmentData(department);

		Department dept = departmentService.create(department);
		return Response.created(createLocation(uriInfo, dept)).lastModified(dept.getLastUpdated()).entity(dept).build();
	}

	@GET
	@Operation(summary = "Get all departments with optional search parameter")
	@APIResponse(responseCode = "200",
			description = "All departments",
			content = @Content(mediaType = MediaType.APPLICATION_JSON,
					schema = @Schema(type = SchemaType.ARRAY, implementation = Department.class)))
	public Response getAll(@QueryParam("name") String name) {
		System.out.format(">>> getAll(%s)%n", name);
		if (name != null && !name.isBlank()) {
			return Response.ok(departmentService.search(name)).build();
		}
		return Response.ok(departmentService.getAll()).build();
	}

	@GET
	@Path("{id}")
	@Operation(summary = "Get a specific department")
	@APIResponse(responseCode = "200",
			description = "The department instance",
			content = @Content(mediaType = MediaType.APPLICATION_JSON,
					schema = @Schema(type = SchemaType.OBJECT, implementation = Department.class)))
	@APIResponse(responseCode = "404", description = "If the department could not be found")
	// FIXME I don't think PermitAll should be required here
	@PermitAll
	public Response get(@PathParam("id") int id) {
		System.out.format(">>> get(%d)%n", Integer.valueOf(id));
		return Response.ok(departmentService.get(id)).build();
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(summary = "Update a department")
	@APIResponse(responseCode = "200",
			description = "The department instance",
			content = @Content(mediaType = MediaType.APPLICATION_JSON,
					schema = @Schema(type = SchemaType.OBJECT, implementation = Department.class)))
	@APIResponse(responseCode = "404", description = "If the department could not be found")
	// FIXME I don't think PermitAll should be required here
	@PermitAll
	public Response update(@Context UriInfo uriInfo, @Valid Department department) {
		System.out.format(">>> update(%d)%n", department.getId());
		List<Employee> employees = department.getEmployees();
		if (employees != null && !employees.isEmpty()) {
			employees.forEach(emp -> emp.setDepartment(department));
		}
		Department dept = departmentService.update(department);
		// FIXME PUT should return no content
		return Response.ok(dept).location(createLocation(uriInfo, department)).build();
	}

	@DELETE
	@Path("{id}")
	@Operation(summary = "Delete a department")
	@APIResponse(responseCode = "204", description = "If the delete was successful")
	@APIResponse(responseCode = "404", description = "If the department could not be found")
	@PermitAll
	public Response delete(@PathParam("id") int id) {
		System.out.format(">>> delete(%d)%n", Integer.valueOf(id));

		departmentService.delete(id);
		return Response.noContent().build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("{id}/employee")
	// FIXME I don't think PermitAll should be required here
	@PermitAll
	public Response addEmploye(@PathParam("id") int departmentId, @Valid Employee employee) {
		System.out.format(">>> addEmploye(%d, %s)%n", Integer.valueOf(departmentId), employee.getName());
		departmentService.addEmploye(departmentId, employee);
		return Response.noContent().build();
	}

	@DELETE
	@Path("{did}/employee/{eid}")
	// FIXME I don't think PermitAll should be required here
	@Operation(summary = "Delete a department")
	@APIResponse(responseCode = "204", description = "If the delete was successful")
	@PermitAll
	public Response removeEmployee(@PathParam("did") int departmentId, @PathParam("eid") int employeeId) {
		System.out.format(">>> removeEmployee(5d, %d)%n", Integer.valueOf(departmentId), Integer.valueOf(employeeId));
		departmentService.removeEmployee(departmentId, employeeId);
		return Response.noContent().build();
	}

	private static URI createLocation(UriInfo uriInfo, Department department) {
		return uriInfo.getAbsolutePathBuilder().path(department.getId().toString()).build();
	}

	private static void testValidateDepartmentData(@Valid Department department) {
		List<Employee> employees = department.getEmployees();
		if (employees != null && !employees.isEmpty()) {
			employees.forEach(emp -> {
				if (emp.getFavouriteDrink() != null && emp.getFavouriteDrink().length() > 20) {
					System.out.println("*** Length of favourite drink: " + emp.getFavouriteDrink().length());
					if (emp.getFavouriteDrink().length() > 30) {
						System.out.println("*** Error: length of favourite drink (" + emp.getFavouriteDrink().length()
								+ ") is greater than 30");
					}
				}
			});
		}
	}
}
