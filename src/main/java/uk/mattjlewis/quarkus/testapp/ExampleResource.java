package uk.mattjlewis.quarkus.testapp;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("hello")
@ApplicationScoped
@SuppressWarnings("static-method")
public class ExampleResource {
	@GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "hello";
    }
}