package sr2_flop_box.flopbox;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.SyncInvoker;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import sr2_flop_box.authenticate.RequestFilter;
import sr2_flop_box.flopbox.Main;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.json.JSONArray;
import org.json.JSONObject;

import jakarta.ws.rs.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

@TestMethodOrder(OrderAnnotation.class)
public class ClientTest {
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
        
        WebTarget resourceWebTarget = target.path("flop-box/server").queryParam("alias", "webtp").queryParam("server", "webtp.fil.univ-lille1.fr").queryParam("port", "21");
        Invocation.Builder invocationBuilder = resourceWebTarget.request().header("login", "sema").header("password", "sdz");
        Response response = invocationBuilder.post(Entity.entity("A string entity to be POSTed", MediaType.TEXT_PLAIN));
    }
   
    @AfterAll
    public static void tearDown() throws Exception {
        //assertEquals(output.toString(), "abcdefghij");
        server.stop();
    }

    @Test
    @Order(1) 
    public void testCreateFolderIntoServerWithBadAuthentification() {
        WebTarget resourceWebTarget = target.path("flop-box/webtp/repartie/projet/create");
        Invocation.Builder invocationBuilder = resourceWebTarget.request().header("login", "sem").header("password", "sdz").header("ftp-login", "altinkaynak").header("ftp-password", "systemerepartie");
        Response response = invocationBuilder.post(Entity.entity("A string entity to be POSTed", MediaType.TEXT_PLAIN));
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        
    }

    @Test
    @Order(2) 
    public void testCreateFolderIntoServer() {
        WebTarget resourceWebTarget = target.path("flop-box/webtp/repartie/create");
        Invocation.Builder invocationBuilder = resourceWebTarget.request().header("login", "sema").header("password", "sdz").header("ftp-login", "altinkaynak").header("ftp-password", "systemerepartie");
        Response response = invocationBuilder.post(Entity.entity("A string entity to be POSTed", MediaType.TEXT_PLAIN));
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        
    }

    @Test
    @Order(3) 
    public void testCreateFolderIntoServerHoweverFolderAlreadyExistERROR() {
        WebTarget resourceWebTarget = target.path("flop-box/webtp/repartie/create");
        Invocation.Builder invocationBuilder = resourceWebTarget.request().header("login", "sema").header("password", "sdz").header("ftp-login", "altinkaynak").header("ftp-password", "systemerepartie");
        Response response = invocationBuilder.post(Entity.entity("A string entity to be POSTed", MediaType.TEXT_PLAIN));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    

    @Test
    @Order(4) 
    public void testDownloadFolderFromServerToClient() {
        WebTarget resourceWebTarget = target.path("flop-box/webtp/systeme/download");
        Invocation.Builder invocationBuilder = resourceWebTarget.request().header("login", "sema").header("password", "sdz").header("ftp-login", "altinkaynak").header("ftp-password", "systemerepartie");
        Response response = invocationBuilder.get();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(response.readEntity(String.class).contains("links"));
    }



    @Test
    @Order(5) 
    public void testGetAllFolderName() {
        WebTarget resourceWebTarget = target.path("flop-box/webtp");
        Invocation.Builder invocationBuilder = resourceWebTarget.request().header("login", "sema").header("password", "sdz").header("ftp-login", "altinkaynak").header("ftp-password", "systemerepartie");
        Response response = invocationBuilder.get();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(response.readEntity(String.class).contains("public_html"));
    }

    @Test
    @Order(6) 
    public void testGetAllFolderSelected() {
        WebTarget resourceWebTarget = target.path("flop-box/webtp/public_html");
        Invocation.Builder invocationBuilder = resourceWebTarget.request().header("login", "sema").header("password", "sdz").header("ftp-login", "altinkaynak").header("ftp-password", "systemerepartie");
        Response response = invocationBuilder.get();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(response.readEntity(String.class).contains("README.md"));
    }

    @Test
    @Order(7) 
    public void testGetAllFolderSelectedDontExistError() {
        WebTarget resourceWebTarget = target.path("flop-box/webtp/html");
        Invocation.Builder invocationBuilder = resourceWebTarget.request().header("login", "sema").header("password", "sdz").header("ftp-login", "altinkaynak").header("ftp-password", "systemerepartie");
        Response response = invocationBuilder.get();
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    @Order(8) 
    public void testAliasNameExistError() {
        WebTarget resourceWebTarget = target.path("flop-box/essai");
        Invocation.Builder invocationBuilder = resourceWebTarget.request().header("login", "sema").header("password", "sdz").header("ftp-login", "altinkaynak").header("ftp-password", "systemerepartie");
        Response response = invocationBuilder.get();
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }


    

    @Test
    @Order(9) 
    public void testUploadFolderFromClientToServer() {
        WebTarget resourceWebTarget = target.path("flop-box/webtp/repartie/dossiers/upload");
        Invocation.Builder invocationBuilder = resourceWebTarget.request().header("login", "sema").header("password", "sdz").header("ftp-login", "altinkaynak").header("ftp-password", "systemerepartie");
        Response response = invocationBuilder.post(Entity.entity("A string entity to be POSTed", MediaType.TEXT_PLAIN));
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertTrue(response.readEntity(String.class).contains("links"));
    }


    @Test
    @Order(10) 
    public void testUploadFolderFromClientToServerBadPathError() {
        WebTarget resourceWebTarget = target.path("flop-box/webtp/reaprtie/dossiers/upload");
        Invocation.Builder invocationBuilder = resourceWebTarget.request().header("login", "sema").header("password", "sdz").header("ftp-login", "altinkaynak").header("ftp-password", "systemerepartie");
        Response response = invocationBuilder.post(Entity.entity("A string entity to be POSTed", MediaType.TEXT_PLAIN));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }


   

    @Test
    @Order(11) 
    public void testRenameFolderIntoServer() {
        WebTarget resourceWebTarget = target.path("flop-box/webtp/systeme/dossier/rename").queryParam("new", "value");
        Invocation.Builder invocationBuilder = resourceWebTarget.request().header("login", "sema").header("password", "sdz").header("ftp-login", "altinkaynak").header("ftp-password", "systemerepartie");
        Response response = invocationBuilder.put(Entity.entity("A string entity to be POSTed", MediaType.TEXT_PLAIN));
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    }


    @Test
    @Order(12) 
    public void testRenameFileIntoServer() {
        WebTarget resourceWebTarget = target.path("flop-box/webtp/public_html/nv.md/rename").queryParam("new", "modif.md");
        Invocation.Builder invocationBuilder = resourceWebTarget.request().header("login", "sema").header("password", "sdz").header("ftp-login", "altinkaynak").header("ftp-password", "systemerepartie");
        Response response = invocationBuilder.put(Entity.entity("A string entity to be POSTed", MediaType.TEXT_PLAIN));
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    }


    @Test
    @Order(13) 
    public void testRenameFileORFolderIntoServerWhoDontExistError() {
        WebTarget resourceWebTarget = target.path("flop-box/webtp/systeme/val/rename").queryParam("new", "value");
        Invocation.Builder invocationBuilder = resourceWebTarget.request().header("login", "sema").header("password", "sdz").header("ftp-login", "altinkaynak").header("ftp-password", "systemerepartie");
        Response response = invocationBuilder.put(Entity.entity("A string entity to be POSTed", MediaType.TEXT_PLAIN));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    @Order(14) 
    public void testDeleteFileOrFolderIntoServer() {
        WebTarget resourceWebTarget = target.path("flop-box/webtp/repartie/delete");
        Invocation.Builder invocationBuilder = resourceWebTarget.request().header("login", "sema").header("password", "sdz").header("ftp-login", "altinkaynak").header("ftp-password", "systemerepartie");
        Response response = invocationBuilder.delete();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }*/
}
