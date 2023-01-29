package sr2_flop_box.manager;

import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jakarta.ws.rs.core.Link;

import org.apache.commons.net.ftp.FTP;

public class FTPClientConnect {
    
    private String actualPath = "/"; 
    private String server;
    private String alias ;
    private FTPClient ftpClient;
    private int port=21; 

    public FTPClientConnect(String alias , String server,int port) {
        this.alias = alias;
        this.server = server;
        this.port = port;
        this.ftpClient = new FTPClient();
    }
    public FTPClientConnect(String alias,String server ) {
        this.server = server;
        this.alias = alias;
        this.ftpClient = new FTPClient();
        
    }

    public void connectToFTPServer() throws Exception{
        this.ftpClient.connect(this.server, this.port);
        int reply = ftpClient.getReplyCode();
        if(!FTPReply.isPositiveCompletion(reply)) {
            ftpClient.disconnect();
            throw new Exception("FTP server refused connection.");
          }
    }
    
    public boolean  loginToFTPServer(String username,String password) throws Exception{

        return ftpClient.login(username, password);
    }

    public void passiveMode(){
        this.ftpClient.enterLocalPassiveMode();
    }

    public void activeMode(){
        this.ftpClient.enterLocalActiveMode();
    }

    public boolean addFileTypeToServer(String filename) throws IOException{
        boolean result =false;

        if(filename.substring(filename.lastIndexOf(".")) == "txt"  || filename.substring(filename.lastIndexOf(".")) == "docx"){

            result =this.ftpClient.setFileType(FTP.ASCII_FILE_TYPE);
        }
        else{
            result = this.ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        }
        return result;
    }

    public String getFileList() throws IOException{
        JSONArray files = new JSONArray();
        for (FTPFile file : ftpClient.listFiles()){
            JSONObject element = new JSONObject();
            element.put("type", file.getType());
            element.put("name", file.getName());
            element.put("size", file.getSize());
            element.put("read", file.hasPermission(FTPFile.USER_ACCESS, FTPFile.READ_PERMISSION));
            element.put("write", file.hasPermission(FTPFile.USER_ACCESS, FTPFile.WRITE_PERMISSION));
            element.put("execute", file.hasPermission(FTPFile.USER_ACCESS, FTPFile.EXECUTE_PERMISSION));
            if(file.isFile()){
                JSONObject element2 = new JSONObject();
                element2.put("Request", "GET");
                element2.put("Content-Type", "multipart/form-data");
                element2.put("file name",file.getName());
                JSONArray links = new JSONArray();
                Link link1 = Link.fromUri("http://localhost:8080/flop-box/{alias}/{path}/download")
                .rel("Request files from the server").type("application/json")
                .build(this.alias,file.getName());
                links.put(new JSONObject().put("href", link1.getUri()).put("rel", link1.getRel()).put("type", "file"));
                element2.put("links", links);
                element.put("download",element2);
            }else{
                JSONObject element1 = new JSONObject();
                element1.put("Request", "GET");
                JSONArray links = new JSONArray();
                Link link1 = Link.fromUri("http://localhost:8080/flop-box/{alias}/{file}/download")
                .rel("Request folder from the server").type("application/json")
                .build(this.alias,"/"+file.getName());
                links.put(new JSONObject().put("href", link1.getUri()).put("rel", link1.getRel()).put("type", "folder"));
                element1.put("links", links);
                element.put("download",element1);
            }
            files.put(element);
        }
        return files.toString();
    }

    public String getFileList(String path) throws IOException{
        JSONArray files = new JSONArray();
        for (FTPFile file : ftpClient.listFiles("/"+path)){
            System.out.println(file.getName());
            JSONObject element = new JSONObject();
            element.put("type", file.getType());
            element.put("name", file.getName());
            element.put("size", file.getSize());
            element.put("read", file.hasPermission(FTPFile.USER_ACCESS, FTPFile.READ_PERMISSION));
            element.put("write", file.hasPermission(FTPFile.USER_ACCESS, FTPFile.WRITE_PERMISSION));
            element.put("execute", file.hasPermission(FTPFile.USER_ACCESS, FTPFile.EXECUTE_PERMISSION));
            if(file.isFile()){
                JSONObject element2 = new JSONObject();
                element2.put("Request", "GET");
                element2.put("Content-Type", "multipart/form-data");
                element2.put("file name",file.getName());
                JSONArray links = new JSONArray();
                Link link1 = Link.fromUri("http://localhost:8080/flop-box/{alias}/{path}/download")
                .rel("Request files from the server").type("application/json")
                .build(this.alias,file.getName());
                links.put(new JSONObject().put("href", link1.getUri()).put("rel", link1.getRel()).put("type", "file"));
                element2.put("links", links);
                element.put("download",element2);
            }else{
                JSONObject element1 = new JSONObject();
                element1.put("Request", "GET");
                JSONArray links = new JSONArray();
                Link link1 = Link.fromUri("http://localhost:8080/flop-box/{alias}/{file}/download")
                .rel("Request folder from the server").type("application/json")
                .build(this.alias,path+"/"+file.getName());
                links.put(new JSONObject().put("href", link1.getUri()).put("rel", link1.getRel()).put("type", "folder"));
                element1.put("links", links);
                element.put("download",element1);
            }
            files.put(element);
        }
        return files.toString();
    }

    public boolean makeDirectoryIntoServer(String path) throws IOException{
         return this.ftpClient.makeDirectory(path);
    }

    public boolean storeFileToServer(InputStream input,String path) throws IOException{
        Boolean situation = this.ftpClient.storeFile(path, input);
        input.close();
        return situation;
    }

    public InputStream retrieveFileFromServer(String filenameToRetrieve ) throws IOException{
        return ftpClient.retrieveFileStream(filenameToRetrieve);    
    }

    public boolean changeToParentDirectoryServer() throws IOException{
        int index = this.actualPath.lastIndexOf("/");
        String change = this.actualPath.substring(0, index-1);
        this.actualPath = change;
        return this.ftpClient.changeToParentDirectory();
    }

    public boolean changeToChildrenDirectoryServer(String pathname) throws IOException{
        if(this.actualPath.equals("/")){
            this.actualPath = "/"+pathname;
        }else{
            this.actualPath = this.actualPath+"/"+pathname;
        }

        return this.ftpClient.changeWorkingDirectory(this.actualPath);
    }

    public boolean logoutFromServer() throws IOException{
        return this.ftpClient.logout();
    }

    public void disconnectFromServer() throws IOException{
        this.ftpClient.disconnect();
    }


    public String getServerName(){
        return this.server;
    }

    public boolean renameFileORFolderInsideServer(String oldname, String newname) throws IOException{
        return ftpClient.rename(oldname, newname);
    }

    public boolean removeFolderIntoServer(String path ) throws IOException{
        ftpClient.changeWorkingDirectory(path);
        if(path.contains(".")){
            return ftpClient.deleteFile(path);
        }
        else{
            for (FTPFile file : ftpClient.listFiles()) {
                
                if (file.isDirectory()) {
                    
                    removeFolderIntoServer(path+"/"+file.getName());
                } else {
                    ftpClient.deleteFile(file.getName());
                }
            }
            ftpClient.changeWorkingDirectory(path);
            ftpClient.changeToParentDirectory();
            return ftpClient.removeDirectory(path);
        }
        
    }


    public String generateLinkForUploadFolder(String path){
        JSONArray retour = new JSONArray();

        JSONObject element = new JSONObject();
        element.put("Request", "POST");
        element.put("Content-Type", "multipart/form-data");
        JSONArray links = new JSONArray();
        Link link1 = Link.fromUri("http://localhost:8080/flop-box/{alias}/{path}/upload/file?name=filename")
            .rel("Upload files into the server").type("application/json")
            .build(this.alias,path);
        links.put(new JSONObject().put("href", link1.getUri()).put("rel", link1.getRel()).put("type", link1.getType()));
        element.put("links", links);

        JSONObject element1 = new JSONObject();
        element1.put("Request", "POST");
        JSONArray links1 = new JSONArray();
        Link link2 = Link.fromUri("http://localhost:8080/flop-box/{alias}/{path}/foldername/upload")
            .rel("Upload folder into the server").type("application/json")
            .build(alias,path);
        links1.put(new JSONObject().put("href", link2.getUri()).put("rel", link2.getRel()).put("type", link2.getType()));
        element1.put("links", links);

        retour.put(element);
        retour.put(element1);

        return retour.toString();
    }


    public String generateLinksForDownloadFolder(String path) throws JSONException, IOException{
        JSONArray files = new JSONArray();
        for (FTPFile file : ftpClient.listFiles("/"+path)){
            if(file.isFile()){
                JSONObject element = new JSONObject();
                element.put("Request", "GET");
                element.put("Content-Type", "multipart/form-data");
                element.put("file name",file.getName());
                JSONArray links = new JSONArray();
                Link link1 = Link.fromUri("http://localhost:8080/flop-box/{alias}/{path}/download")
                .rel("Request files from the server").type("application/json")
                .build(this.alias,path);
                links.put(new JSONObject().put("href", link1.getUri()).put("rel", link1.getRel()).put("type", "file"));
                element.put("links", links);
                files.put(element);
            }else{
                JSONObject element = new JSONObject();
                element.put("Request", "GET");
                JSONArray links = new JSONArray();
                Link link1 = Link.fromUri("http://localhost:8080/flop-box/{alias}/{file}/download")
                .rel("Request folder from the server").type("application/json")
                .build(this.alias,path+"/"+file.getName());
                links.put(new JSONObject().put("href", link1.getUri()).put("rel", link1.getRel()).put("type", "folder"));
                element.put("links", links);
                files.put(element);
            }
            
        }
        return files.toString();
    }
}
