package sr2_flop_box.authenticate;

import java.io.IOException;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import sr2_flop_box.manager.UserManager;

/**
 * Handler pour l'authentification
 */
@Authentification
@Provider
public class RequestFilter implements ContainerRequestFilter{

    private UserManager manager = new UserManager();

    /**
     * Fonction de filtre de verification des utilisateurs lors des requetes
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if(!(this.manager.checkUserExistAndPasswordIsCorrect(requestContext.getHeaderString("login"), requestContext.getHeaderString("password")))){
            requestContext.abortWith((Response.status(Response.Status.UNAUTHORIZED).entity("the user doesn't exist or the password is uncorrect, please suscribe into the application or write the correct password!!").build()));
        }
        
    }
    
}

