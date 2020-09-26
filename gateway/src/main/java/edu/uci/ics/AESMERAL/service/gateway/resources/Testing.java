package edu.uci.ics.AESMERAL.service.gateway.resources;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("hello")
public class Testing {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response hello(@Context HttpHeaders headers){
        System.err.println(headers.getAcceptableMediaTypes());
        return Response.status(Response.Status.OK).entity("Hello").build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("world")
    public Response world(){
        return Response.status(Response.Status.OK).entity("Hello World").build();
    }
}
