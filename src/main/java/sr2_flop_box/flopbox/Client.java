package sr2_flop_box.flopbox;

import sr2_flop_box.authenticate.Authentification;
import sr2_flop_box.manager.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;  
import org.glassfish.jersey.media.multipart.FormDataParam;

import jakarta.ws.rs.*;


/**
 * Classe permettant la communication avec les serveurs
 */
@Authentification
@Path("/flop-box")
public class Client {

    private ServerManager servers = new ServerManager();
    private FTPClientConnect ftp;

    /**
     * fonction permettant de récuperer les noms des dossiers racine d'un serveur FTP
     * @param name nom de l'utilisateur
     * @param pass mot de passe 
     * @param alias alias du serveur 
     * @return Response
     * @throws NumberFormatException
     * @throws Exception
     */
    @Path("/{alias}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllFolderName(@HeaderParam("ftp-login") String name, @HeaderParam("ftp-password") String pass,@PathParam("alias") String alias) throws NumberFormatException,Exception{
        String serverContent = servers.getServerAssociation(alias);
        if(serverContent!=""){
            String[] elements = serverContent.split(":");
            this.ftp = new FTPClientConnect( elements[0],elements[1],Integer.parseInt(elements[2]));
            ftp.connectToFTPServer();
            ftp.loginToFTPServer(name, pass);
            ftp.passiveMode();
            String element = ftp.getFileList();
            ftp.logoutFromServer();
            ftp.disconnectFromServer();
            return Response.status(Response.Status.OK).entity(element).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).entity("The alias doesn't exist").build();
        
       
    }
    /**
     * fonction permettant de récuperer les noms des dossiers d'un chemin specifique d'un serveur FTP
     * @param name nom de l'utilisateur
     * @param pass mot de passe 
     * @param alias alias du serveur 
     * @param path chemin valide
     * @return
     * @throws NumberFormatException
     * @throws IOException
     * @throws Exception
     */
    @Path("/{alias}/{path: .*}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllFolderSelected(@HeaderParam("ftp-login") String name, @HeaderParam("ftp-password") String pass,@PathParam("alias") String alias , @PathParam("path") String path) throws NumberFormatException, IOException, Exception{
        String serverContent = servers.getServerAssociation(alias);
        if(serverContent!=""){
            String[] elements = serverContent.split(":");
            this.ftp = new FTPClientConnect( elements[0],elements[1],Integer.parseInt(elements[2]));
            ftp.connectToFTPServer();;
            ftp.loginToFTPServer(name, pass);
            ftp.passiveMode();
            boolean change = ftp.changeToChildrenDirectoryServer(path);
            String liste = ftp.getFileList();
            ftp.logoutFromServer();
            ftp.disconnectFromServer();
            if(change){
                return Response.status(Response.Status.OK).entity(liste).build();
            }
            else{
                return Response.status(Response.Status.BAD_REQUEST).entity("The path don't exist, please write the right path").build();
            }
        }
        return Response.status(Response.Status.BAD_REQUEST).entity("The alias doesn't exist").build();
    }


    /**
     * renommer un dossier ou fichier d'un serveur
     * @param name nom de l'utilisateur
     * @param pass mot de passe 
     * @param alias alias du serveur 
     * @param path chemin valide
     * @param newname nouveau nom
     * @return Response
     * @throws NumberFormatException
     * @throws IOException
     * @throws Exception
     */
    @Path("/{alias}/{path: .+}/rename")
    @PUT
    public Response renameFileORFolderIntoServer(@HeaderParam("ftp-login") String name, @HeaderParam("ftp-password") String pass,@PathParam("alias") String alias , @PathParam("path") String path,@QueryParam("new") String newname) throws NumberFormatException, IOException, Exception{
        
        String serverContent = servers.getServerAssociation(alias);
        if(serverContent!=""){
            String[] elements = serverContent.split(":");
            this.ftp = new FTPClientConnect( elements[0],elements[1],Integer.parseInt(elements[2]));
            ftp.connectToFTPServer();
            ftp.loginToFTPServer(name, pass);
            ftp.passiveMode();
            String nouvelle="";
            if(path.contains("/")){
                nouvelle = "/"+path.substring(0, path.lastIndexOf("/"))+ "/"+ newname;
            }else{
                nouvelle = "/"+newname;
            }
            boolean great = ftp.renameFileORFolderInsideServer("/" +path , nouvelle );
            ftp.logoutFromServer();
            ftp.disconnectFromServer();
            
            if(great){
                return Response.status(Response.Status.CREATED).build();
            }
            else{
                return Response.status(Response.Status.BAD_REQUEST).entity("The folder or file doesn't exist, please specify the right folder or file name").build();
            }
        }
        return Response.status(Response.Status.BAD_REQUEST).entity("The alias doesn't exist").build();
    }

    /**
     * suppression d'un fichier ou dossier d'un serveur
     * @param name nom de l'utilisateur
     * @param pass mot de passe 
     * @param alias alias du serveur 
     * @param path chemin valide
     * @return
     * @throws NumberFormatException
     * @throws IOException
     * @throws Exception
     */
    @Path("/{alias}/{path: .+}/delete")
    @DELETE
    public Response deleteFileOrFolderIntoServer(@HeaderParam("ftp-login") String name, @HeaderParam("ftp-password") String pass,@PathParam("alias") String alias , @PathParam("path") String path) throws NumberFormatException, IOException, Exception{
        String serverContent = servers.getServerAssociation(alias);
        if(serverContent!=""){
            String[] elements = serverContent.split(":");
            this.ftp = new FTPClientConnect( elements[0],elements[1],Integer.parseInt(elements[2]));
            this.ftp.connectToFTPServer();
            ftp.loginToFTPServer(name, pass);
            ftp.passiveMode();
            boolean great = ftp.removeFolderIntoServer("/"+path);
            ftp.logoutFromServer();
            ftp.disconnectFromServer();
            if(great){
                return Response.status(Response.Status.OK).build();
            }
            else{
                return Response.status(Response.Status.BAD_REQUEST).entity("The folder or file doesn't exist, please specify the right folder or file name").build();
            }
        }
        return Response.status(Response.Status.BAD_REQUEST).entity("The alias doesn't exist").build();
    }


    /**
     * création d'un dossier dans le serveur
     * @param name nom de l'utilisateur
     * @param pass mot de passe 
     * @param alias alias du serveur 
     * @param path chemin valide
     * @return
     * @throws NumberFormatException
     * @throws IOException
     * @throws Exception
     */
    @Path("/{alias}/{path: .+}/create")
    @POST
    public Response createFolderIntoServer(@HeaderParam("ftp-login") String name, @HeaderParam("ftp-password") String pass,@PathParam("alias") String alias , @PathParam("path") String path) throws NumberFormatException, IOException, Exception{
        
        String serverContent = servers.getServerAssociation(alias);
        if(serverContent!=""){
            String[] elements = serverContent.split(":");
            this.ftp = new FTPClientConnect( elements[0],elements[1],Integer.parseInt(elements[2]));
            ftp.connectToFTPServer();
            ftp.loginToFTPServer(name, pass);
            ftp.passiveMode();
            boolean great = ftp.makeDirectoryIntoServer("/"+path);
            ftp.logoutFromServer();
            ftp.disconnectFromServer();
            if(great){
                return Response.status(Response.Status.CREATED).build();
            }
            else{
                return Response.status(Response.Status.BAD_REQUEST).entity("The file hasn't upload, please right the right path to upload").build();
            }
        }
        return Response.status(Response.Status.BAD_REQUEST).entity("The alias doesn't exist").build();
    }

    /**
     * upload d'un fichier dans le serveur
     * @param name nom de l'utilisateur
     * @param pass mot de passe 
     * @param alias alias du serveur 
     * @param path chemin valide
     * @param uploadedInputStream
     * @param fileDetail
     * @return
     * @throws NumberFormatException
     * @throws Exception
     */
    @Path("/{alias}/{path: .+}/upload/file")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFileFromClientToServer(@HeaderParam("ftp-login") String name, @HeaderParam("ftp-password") String pass,@PathParam("alias") String alias , @PathParam("path") String path,@FormDataParam("file") InputStream uploadedInputStream,@FormDataParam("file") FormDataContentDisposition fileDetail) throws NumberFormatException,Exception{
        
        String serverContent = servers.getServerAssociation(alias);
        if(serverContent!=""){
            String[] elements = serverContent.split(":");
            this.ftp = new FTPClientConnect( elements[0],elements[1],Integer.parseInt(elements[2]));
            ftp.connectToFTPServer();
            ftp.loginToFTPServer(name, pass);
            ftp.passiveMode();
            ftp.addFileTypeToServer(fileDetail.getFileName());
            ftp.changeToChildrenDirectoryServer(path);
            boolean great = ftp.storeFileToServer(uploadedInputStream, fileDetail.getFileName());
            ftp.logoutFromServer();
            ftp.disconnectFromServer();
            if(great){
                return Response.status(Response.Status.CREATED).build();
            }
            else{
                return Response.status(Response.Status.BAD_REQUEST).entity("The file hasn't upload, please write the right path to upload").build();
            }
        }
        return Response.status(Response.Status.BAD_REQUEST).entity("The alias doesn't exist").build();
    }

    /**
     * upload d'un dossier 
     * @param name nom de l'utilisateur
     * @param pass mot de passe 
     * @param alias alias du serveur 
     * @param path chemin valide
     * @return
     * @throws NumberFormatException
     * @throws Exception
     */
    @Path("/{alias}/{path: .+}/upload")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadFolderFromClientToServer(@HeaderParam("ftp-login") String name, @HeaderParam("ftp-password") String pass,@PathParam("alias") String alias , @PathParam("path") String path) throws NumberFormatException,Exception{
        
        String serverContent = servers.getServerAssociation(alias);
        if(serverContent!=""){
            String[] elements = serverContent.split(":");
            this.ftp = new FTPClientConnect( elements[0],elements[1],Integer.parseInt(elements[2]));
            ftp.connectToFTPServer();
            ftp.loginToFTPServer(name, pass);
            ftp.passiveMode();
            boolean great = ftp.makeDirectoryIntoServer("/"+path);
            String json = ftp.generateLinkForUploadFolder(path);
            ftp.logoutFromServer();
            ftp.disconnectFromServer();
            if(great){
                return Response.status(Response.Status.CREATED).entity(json).build();
            }
            else{
                return Response.status(Response.Status.BAD_REQUEST).entity("The folder hasn't generate, please write the right path to upload").build();
            }
        }
        return Response.status(Response.Status.BAD_REQUEST).entity("The alias doesn't exist").build();
    }


    /**
     * telechargement d'un fichier du serveur
     * @param name nom de l'utilisateur
     * @param pass mot de passe 
     * @param alias alias du serveur 
     * @param path chemin valide
     * @param filename
     * @return
     * @throws NumberFormatException
     * @throws Exception
     */
    @Path("/{alias}/{path: .+}/download/file")
    @GET
    @Produces(MediaType.MULTIPART_FORM_DATA)
    public Response downloadFileFromServerToClient(@HeaderParam("ftp-login") String name, @HeaderParam("ftp-password") String pass,@PathParam("alias") String alias , @PathParam("path") String path,@QueryParam("name") String filename) throws NumberFormatException,Exception{
        
        String serverContent = servers.getServerAssociation(alias);
        if(serverContent!=""){
            String[] elements = serverContent.split(":");
            this.ftp = new FTPClientConnect( elements[0],elements[1],Integer.parseInt(elements[2]));
            ftp.connectToFTPServer();
            ftp.loginToFTPServer(name, pass);
            ftp.passiveMode();
            ftp.addFileTypeToServer(filename);
            ftp.changeToChildrenDirectoryServer(path);
            InputStream file = ftp.retrieveFileFromServer("/"+path+"/"+filename);
            ftp.logoutFromServer();
            ftp.disconnectFromServer();
            return Response.status(Response.Status.OK).entity(file).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).entity("The alias doesn't exist").build();  
    }
    
    
    /**
     * telechargement d'un dossier du serveur
     * @param name
     * @param pass
     * @param alias
     * @param path
     * @return
     * @throws NumberFormatException
     * @throws Exception
     */
    @Path("/{alias}/{path: .+}/download")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response downloadFolderFromServerToClient(@HeaderParam("ftp-login") String name, @HeaderParam("ftp-password") String pass,@PathParam("alias") String alias , @PathParam("path") String path) throws NumberFormatException,Exception{
        
        String serverContent = servers.getServerAssociation(alias);
        if(serverContent!=""){
            String[] elements = serverContent.split(":");
            this.ftp = new FTPClientConnect( elements[0],elements[1],Integer.parseInt(elements[2]));
            ftp.connectToFTPServer();
            ftp.loginToFTPServer(name, pass);
            ftp.passiveMode();
            String json = ftp.generateLinksForDownloadFolder(path);
            ftp.logoutFromServer();
            ftp.disconnectFromServer();
            return Response.status(Response.Status.OK).entity(json).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).entity("The alias doesn't exist").build();  
    }
}
