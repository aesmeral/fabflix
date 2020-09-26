package edu.uci.ics.AESMERAL.service.gateway.threadpool;

import javax.ws.rs.Path;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Map;

public class ClientRequest
{
    /* User Information */
    private String email;
    private String session_id;
    private String transaction_id;

    /* Target Service and Endpoint */
    private String URI;
    private String endpoint;
    private HTTPMethod method;

    private Map<String, List<String>> queryParams = null;


    /*
     * So before when we wanted to get the request body
     * we would grab it as a String (String jsonText).
     *
     * The Gateway however does not need to see the body
     * but simply needs to pass it. So we save ourselves some
     * time and overhead by grabbing the request as a byte array
     * (byte[] jsonBytes).
     *
     * This way we can just act as a
     * messenger and just pass along the bytes to the target
     * service and it will do the rest.
     *
     * for example:
     *
     * where we used to do this:
     *
     *     @Path("hello")
     *     ...ect
     *     public Response hello(String jsonString) {
     *         ...ect
     *     }
     *
     * do:
     *
     *     @Path("hello")
     *     ...ect
     *     public Response hello(byte[] jsonBytes) {
     *         ...ect
     *     }
     *
     */
    private byte[] requestBytes;

    public ClientRequest()
    {

    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public void setURI(String URI) {
        this.URI = URI;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void setMethod(HTTPMethod method) {
        this.method = method;
    }

    public void setRequestBytes(byte[] requestBytes) {
        this.requestBytes = requestBytes;
    }

    public String getEmail() {
        return email;
    }

    public String getSession_id() {
        return session_id;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public String getURI() {
        return URI;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public HTTPMethod getMethod() {
        return method;
    }

    public byte[] getRequestBytes() {
        return requestBytes;
    }
    public Map<String, List<String>> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(Map<String, List<String>> queryParams) {
        this.queryParams = queryParams;
    }
}
