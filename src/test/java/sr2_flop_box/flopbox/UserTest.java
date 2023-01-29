package sr2_flop_box.flopbox;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.HttpServer;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;


import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTest {
     
    private static HttpServer server;
    private static WebTarget target;

    @BeforeAll
    public static void setUp() throws Exception {
        // start the server
        server = Main.startServer();
        // create the client
        Client c = ClientBuilder.newClient();

        // uncomment the following line if you want to enable
        // support for JSON in the client (you also have to uncomment
        // dependency on jersey-media-json module in pom.xml and Main.startServer())
        // --
        // c.configuration().enable(new org.glassfish.jersey.media.json.JsonJaxbFeature());

        target = c.target("http://localhost:8080/");
    }

    @AfterAll
    public static void tearDown() throws Exception {
        server.stop();
    }

    @Test
    public void testAddUser() {

        Response resp = target.path("flop-box/auth")
                .request()
                .header("username","userTest")
                .header("password","userTest")
                .post(Entity.entity("A string entity to be POSTed", MediaType.TEXT_PLAIN));
        assertEquals(Response.Status.CREATED.getStatusCode(), resp.getStatus());
    }

    @Test
    public void testAddUserWhoAlreadyExistERROR() {

        target.path("flop-box/auth")
                .request()
                .header("username","userForTest")
                .header("password","userForTest")
                .post(Entity.entity("A string entity to be POSTed", MediaType.TEXT_PLAIN));
        Response respfinal  = target.path("flop-box/auth")
                .request()
                .header("username","userForTest")
                .header("password","userForTest")
                .post(Entity.entity("A string entity to be POSTed", MediaType.TEXT_PLAIN));
        assertEquals(Response.Status.CONFLICT.getStatusCode(), respfinal.getStatus());
    }

    @Test
    public void testDeleteUser() {
        Response resp = target.path("flop-box/auth")
                .request()
                .header("username","userTest")
                .header("password","userTest")
                .delete();
        assertEquals(Response.Status.ACCEPTED.getStatusCode(), resp.getStatus());
    }
}
