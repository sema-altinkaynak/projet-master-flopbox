package sr2_flop_box.flopbox;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import sr2_flop_box.manager.*;

import java.io.IOException;

import jakarta.ws.rs.*;

/**
 * classe qui gere les utilisateurs 
 */
@Path("/flop-box/auth")
public class User {
    
    UserManager manager = new UserManager();

    /**
     * ajout d'un utilisateur dans la base
     * @param name
     * @param password
     * @return
     * @throws IOException
     */
    @POST
    public Response addUser(@HeaderParam("username") String name, @HeaderParam("password") String password) throws IOException{
        boolean create = manager.createUserIntoApplication(name,password);
        if(!(create)){
            return Response.status(Response.Status.CONFLICT)
			.entity("the user already exist into the server")
			.build();

        }
        return Response.status(Response.Status.CREATED)
			.entity("the user is created")
			.build();
    }

    /**
     * supression d'un utilisateur dans la base
     * @param name
     * @param password
     * @return
     * @throws IOException
     */
    @DELETE
    public Response deleteUser(@HeaderParam("username") String name, @HeaderParam("password") String password) throws IOException{
        manager.deleteUserIntoApplication(name, password);
        return Response.status(Response.Status.ACCEPTED)
			.entity("the user is deleted.")
			.build();
    }

}
