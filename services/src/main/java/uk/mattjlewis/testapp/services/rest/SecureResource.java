package uk.mattjlewis.testapp.services.rest;

import java.security.Principal;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Path("secured")
@RequestScoped
@Produces(MediaType.TEXT_PLAIN)
@SuppressWarnings("static-method")
public class SecureResource {
	private static final String USER_ROLE = "Application/User";
	private static final String ADMIN_ROLE = "Application/Admin";

	@GET
	@Path("permit-all")
	public Response permitAll(@Context SecurityContext context) {
		return buildResponse(context);
	}

	@GET
	@Path("admin")
	@RolesAllowed(ADMIN_ROLE)
	public Response admin(@Context SecurityContext context) {
		return buildResponse(context);
	}

	@GET
	@Path("user")
	@RolesAllowed(USER_ROLE)
	public Response user(@Context SecurityContext context) {
		return buildResponse(context);
	}

	private static Response buildResponse(SecurityContext context) {
		Principal caller = context.getUserPrincipal();
		String caller_name = caller == null ? "Anonymous" : caller.getName();
		return Response.ok(String.format("Hello %s; Is secure: %b; Auth scheme: %s; Admin role: %b; User role: %b",
				caller_name, Boolean.valueOf(context.isSecure()), context.getAuthenticationScheme(),
				Boolean.valueOf(context.isUserInRole(ADMIN_ROLE)), Boolean.valueOf(context.isUserInRole(USER_ROLE))))
				.build();
	}
}
