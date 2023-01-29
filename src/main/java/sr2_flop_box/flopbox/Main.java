package sr2_flop_box.flopbox;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import sr2_flop_box.authenticate.RequestFilter;

import java.net.URI;


/**
 * Main class.
 *
 */
/** */
public class Main {
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:8080/";

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    
    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers
        // in sr2_flop_box.projet1 package
        final ResourceConfig rc = new ResourceConfig().packages("sr2_flop_box.flopbox");
        rc.register(MultiPartFeature.class);
        rc.register(RequestFilter.class);
        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    /**
     * Main method.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with endpoints available at "
                + "%s%nHit Ctrl-C to stop it...", BASE_URI));
        System.in.read();
        server.stop();
      

    }
}

