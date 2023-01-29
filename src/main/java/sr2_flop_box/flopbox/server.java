package sr2_flop_box.flopbox;

import sr2_flop_box.authenticate.Authentification;
import sr2_flop_box.manager.*;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.IOException;

import jakarta.ws.rs.*;

/**
 * classe qui gere les serveurs et leur alias dans la base de données
 */
@Authentification
@Path("/flop-box/server")
public class server {
    
    private ServerManager manager = new ServerManager();

    
    /**
     * ajout d'un serveur et son alias
     * @param alias
     * @param server
     * @param port
     * @return
     * @throws IOException
     */
    @POST
    public Response addServerAndAlias(@QueryParam("alias") String alias, @QueryParam("server") String server, @QueryParam("port") String port) throws IOException{
        if(!(manager.addServerToApplication(alias, server, port))){
            return Response.status(Response.Status.CONFLICT)
            .entity("the alias already exist into the server or the server has already added into the application, please do a GET request to have the alias")
            .build();

        }else{
            return Response.status(Response.Status.CREATED)
        .entity("the association is created")
        .build();
        }
        
    }

    /**
     * suppresion d'un serveur et son alias
     * @param alias
     * @param server
     * @return
     * @throws IOException
     */
    @DELETE
    public Response deleteAssociation(@QueryParam("alias") String alias, @QueryParam("server") String server) throws IOException{
        manager.deleteServerIntoApplication(alias, server);
        return Response.status(Response.Status.OK)
            .entity("the user is deleted.")
            .build();
        
    }

    /**
     * recuperation des serveur et leur alias
     * @return
     * @throws IOException
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllAssociation() throws IOException{
        return Response.status(Response.Status.OK)
            .entity(manager.getAllServerAssociation()).type(MediaType.APPLICATION_JSON)
            .build();
        
    }

    /**
     * changement de port de connexion à un serveur
     * @param alias
     * @param port
     * @return
     * @throws IOException
     */
    @Path("/rename/port")
    @PUT
    public Response changePortAssociationWithServer(@QueryParam("alias") String alias, @QueryParam("port") String port) throws IOException{
        if(!manager.changeServerPortIntoApplication(alias, port)){
            return Response.status(Response.Status.BAD_REQUEST)
            .entity("the alias doesn't exist into the server")
            .build();
        }

        return Response.status(Response.Status.CREATED)
            .entity("the port is modified.")
            .build();

        
    }

    /**
     * changement d'un alias
     * @param alias
     * @param aliasNewName
     * @return
     * @throws IOException
     */
    @Path("/rename/alias")
    @PUT
    public Response changerAliasNameWithServerAssociation(@QueryParam("alias") String alias, @QueryParam("aliasNewName") String aliasNewName) throws IOException{
        
        if(!manager.changeServerAliasIntoApplication(alias, aliasNewName)){
            return Response.status(Response.Status.BAD_REQUEST)
            .entity("the alias doesn't exist into the server")
            .build();
        }

        return Response.status(Response.Status.CREATED)
            .entity("the alias is modified.")
            .build();
        
    }

    /**
     * changement du nom de serveur
     * @param alias
     * @param server
     * @return
     * @throws IOException
     */
    @Path("/rename/server")
    @PUT
    public Response changerServerNameAssociationWithAlias(@QueryParam("alias") String alias, @QueryParam("server") String server) throws IOException{

        if(!manager.changeServerIntoApplication(alias, server)){
            return Response.status(Response.Status.BAD_REQUEST)
            .entity("the association doesn't exist into the server")
            .build();
        }

        return Response.status(Response.Status.CREATED)
            .entity("the server is modified.")
            .build();
        
    }

}
