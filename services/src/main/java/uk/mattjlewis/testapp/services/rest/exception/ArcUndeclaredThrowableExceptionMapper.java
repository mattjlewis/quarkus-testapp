package uk.mattjlewis.testapp.services.rest.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import io.quarkus.arc.ArcUndeclaredThrowableException;

@Provider
public class ArcUndeclaredThrowableExceptionMapper implements ExceptionMapper<ArcUndeclaredThrowableException> {
	@Override
	public Response toResponse(ArcUndeclaredThrowableException e) {
		return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
	}
}
