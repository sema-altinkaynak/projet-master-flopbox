package sr2_flop_box.flopbox;


import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import sr2_flop_box.authenticate.RequestFilter;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.client.ClientConfig;
import org.json.JSONArray;
import org.json.JSONObject;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import static org.junit.jupiter.api.Assertions.assertEquals;



@TestMethodOrder(OrderAnnotation.class)
public class serverTest {
/** 
   
    private static HttpServer server;
    private static WebTarget target;
    private static StringBuilder output = new StringBuilder("");

    @BeforeAll
    public static void setUp() throws Exception {
        // start the server
        server = Main.startServer();
        // create the client
        Client c = ClientBuilder.newClient(new ClientConfig()
        .register(RequestFilter.class));

        // uncomment the following line if you want to enable
        // support for JSON in the client (you also have to uncomment
        // dependency on jersey-media-json module in pom.xml and Main.startServer())
        // --
        // c.configuration().enable(new org.glassfish.jersey.media.json.JsonJaxbFeature());

        target = c.target("http://localhost:8080/");
    }

    @AfterAll
    public static void tearDown() throws Exception {
        assertEquals(output.toString(), "abcdefghij");
        server.stop();
    }


   
    @Test
    @Order(1) 
    public void testAddServerAndAlias() {
        output.append("a");
        WebTarget resourceWebTarget = target.path("flop-box/server").queryParam("alias", "ubuntu").queryParam("server", "ftp.ubuntu.com").queryParam("port", "21");
        Invocation.Builder invocationBuilder = resourceWebTarget.request().header("login", "sema").header("password", "sdz");
        Response response = invocationBuilder.post(Entity.entity("A string entity to be POSTed", MediaType.TEXT_PLAIN));
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    }

    @Test
    @Order(2) 
    public void testAddServerAndAliasError() {
        output.append("b");
        WebTarget resourceWebTarget = target.path("flop-box/server").queryParam("alias", "ubuntu").queryParam("server", "ftp.ubuntu.com").queryParam("port", "21");
        Invocation.Builder invocationBuilder = resourceWebTarget.request().header("login", "sema").header("password", "sdz");
        Response response = invocationBuilder.post(Entity.entity("A string entity to be POSTed", MediaType.TEXT_PLAIN));
        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
    }

    @Test
    @Order(3) 
    public void testGetAllAssociation() {
        output.append("c");
        WebTarget resourceWebTarget = target.path("flop-box/server");
        Invocation.Builder invocationBuilder = resourceWebTarget.request().header("login", "sema").header("password", "sdz");
        Response response = invocationBuilder.get();
        JSONArray server = new JSONArray();
        JSONObject element = new JSONObject();
        element.put("alias", "ubuntu");
        element.put("server","ftp.ubuntu.com" );
        element.put("port", "21");
        JSONArray links = new JSONArray();
        Link link1 = Link.fromUri("http://localhost:8080/flop-box/{alias}")
            .rel("Request files's names from the server").type("application/json")
            .build("ubuntu");
        links.put(new JSONObject().put("href", link1.getUri()).put("rel", link1.getRel()).put("type", link1.getType()));
        element.put("links", links);
        server.put(element);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(server.toString().contains("server"), response.readEntity(String.class).contains("server"));
    }

    @Test
    @Order(4) 
    public void testChangePortAssociationWithServer() {
        output.append("d");
        WebTarget resourceWebTarget = target.path("flop-box/server/rename/port").queryParam("alias", "ubuntu").queryParam("port", "31");
        Invocation.Builder invocationBuilder = resourceWebTarget.request().header("login", "sema").header("password", "sdz");
        Response response = invocationBuilder.put(Entity.entity("A string entity to be POSTed", MediaType.TEXT_PLAIN));
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    }
    

    @Test
    @Order(5) 
    public void testChangePortAssociationWithServerErrorAliasDoesntExist() {
        output.append("e");
        WebTarget resourceWebTarget = target.path("flop-box/server/rename/port").queryParam("alias", "meo").queryParam("port", "31");
        Invocation.Builder invocationBuilder = resourceWebTarget.request().header("login", "sema").header("password", "sdz");
        Response response = invocationBuilder.put(Entity.entity("A string entity to be POSTed", MediaType.TEXT_PLAIN));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }


    @Test
    @Order(6) 
    public void testChangerServerNameAssociationWithAlias() {
        output.append("f");
        WebTarget resourceWebTarget = target.path("flop-box/server/rename/server").queryParam("alias", "ubuntu").queryParam("server", "ftp.meo.fr");
        Invocation.Builder invocationBuilder = resourceWebTarget.request().header("login", "sema").header("password", "sdz");
        Response response = invocationBuilder.put(Entity.entity("A string entity to be POSTed", MediaType.TEXT_PLAIN));
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    }

    @Test
    @Order(7) 
    public void testChangerServerNameAssociationWithAliasErrorDoesntExist() {
        output.append("g");
        WebTarget resourceWebTarget = target.path("flop-box/server/rename/server").queryParam("alias", "meo").queryParam("server", "ftp.meo.fr");
        Invocation.Builder invocationBuilder = resourceWebTarget.request().header("login", "sema").header("password", "sdz");
        Response response = invocationBuilder.put(Entity.entity("A string entity to be POSTed", MediaType.TEXT_PLAIN));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }


    @Test
    @Order(8) 
    public void testchangerAliasNameWithServerAssociation() {
        output.append("h");
        WebTarget resourceWebTarget = target.path("flop-box/server/rename/alias").queryParam("alias", "ubuntu").queryParam("aliasNewName", "lille");
        Invocation.Builder invocationBuilder = resourceWebTarget.request().header("login", "sema").header("password", "sdz");
        Response response = invocationBuilder.put(Entity.entity("A string entity to be POSTed", MediaType.TEXT_PLAIN));
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    }

    @Test
    @Order(9) 
    public void testchangerAliasNameWithServerAssociationErrorDoesntExist() {
        output.append("i");
        WebTarget resourceWebTarget = target.path("flop-box/server/rename/alias").queryParam("alias", "ubuntu").queryParam("aliasNewName", "lille");
        Invocation.Builder invocationBuilder = resourceWebTarget.request().header("login", "sema").header("password", "sdz");
        Response response = invocationBuilder.put(Entity.entity("A string entity to be POSTed", MediaType.TEXT_PLAIN));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    @Order(10) 
    public void testDeleteAssociation() {
        output.append("j");
        WebTarget resourceWebTarget = target.path("flop-box/server").queryParam("alias", "lille").queryParam("server", "ftp.meo.fr");
        Invocation.Builder invocationBuilder = resourceWebTarget.request().header("login", "sema").header("password", "sdz");
        Response response = invocationBuilder.delete();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }
    
  
    */
}
