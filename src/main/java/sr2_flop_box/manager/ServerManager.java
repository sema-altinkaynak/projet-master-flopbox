package sr2_flop_box.manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import jakarta.ws.rs.core.Link;


public class ServerManager {
    
    public boolean addServerToApplication(String alias, String serverName,String port) throws IOException{
        File doc = new File("/Users/semaealtinkaynak/Documents/sr2/projet1/src/main/java/sr2_flop_box/manager/data/serveur.txt");

        BufferedReader obj = new BufferedReader(new FileReader(doc));
        boolean in=true;
        String user;
        while ((user = obj.readLine()) != null){
            String[] login = user.split(":");
            if(login[0].equals(alias)){
                
            }
            if(login[1].equals(serverName)){
                in = false;
            }
        }
        obj.close();
        if(in){
            FileWriter fichier = new FileWriter("/Users/semaealtinkaynak/Documents/sr2/projet1/src/main/java/sr2_flop_box/manager/data/serveur.txt",true);
            fichier.write(alias +":"+serverName+":"+port +"\n");
            fichier.close();
        }
        return in;
    }

    public boolean deleteServerIntoApplication(String alias, String serverName) throws IOException{
        FileWriter ecrivain; 

        ecrivain = new FileWriter("/Users/semaealtinkaynak/Documents/sr2/projet1/src/main/java/sr2_flop_box/manager/data/serveur1.txt"); 

        File doc = new File("/Users/semaealtinkaynak/Documents/sr2/projet1/src/main/java/sr2_flop_box/manager/data/serveur.txt");

        BufferedReader obj = new BufferedReader(new FileReader(doc));
        String user;
        while ((user = obj.readLine()) != null){
            String[] login = user.split(":");
            if(!(login[0].equals(alias))){
                ecrivain.write(user+"\n");
            }
        }
        obj.close();
        doc.delete();
        ecrivain.close();
        File file = new File("/Users/semaealtinkaynak/Documents/sr2/projet1/src/main/java/sr2_flop_box/manager/data/serveur1.txt");

        File rename = new File("/Users/semaealtinkaynak/Documents/sr2/projet1/src/main/java/sr2_flop_box/manager/data/serveur.txt");

        file.renameTo(rename);
        return true;

    }

    public boolean checkServerExist(String alias) throws IOException{
        File doc = new File("/Users/semaealtinkaynak/Documents/sr2/projet1/src/main/java/sr2_flop_box/manager/data/serveur.txt");

        BufferedReader obj = new BufferedReader(new FileReader(doc));
        boolean in=false;
        String user;
        while ((user = obj.readLine()) != null){
            String[] login = user.split(":");
            if(login[0].equals(alias)){
                in = true;
            }
        }
        obj.close();
        return in;
    }

    public String getServerAssociation(String alias) throws IOException{

        File doc = new File("/Users/semaealtinkaynak/Documents/sr2/projet1/src/main/java/sr2_flop_box/manager/data/serveur.txt");

        BufferedReader obj = new BufferedReader(new FileReader(doc));
        String servername="";
        String user;
        while ((user = obj.readLine()) != null){
            String[] login = user.split(":");
            if(login[0].equals(alias)){
                servername = user;
            }
        }
        obj.close();
        return servername;
      
        
    }

    public boolean changeServerAliasIntoApplication(String alias, String newAlias) throws IOException{
        FileWriter ecrivain; 

        ecrivain = new FileWriter("/Users/semaealtinkaynak/Documents/sr2/projet1/src/main/java/sr2_flop_box/manager/data/serveur1.txt"); 

        File doc = new File("/Users/semaealtinkaynak/Documents/sr2/projet1/src/main/java/sr2_flop_box/manager/data/serveur.txt");

        BufferedReader obj = new BufferedReader(new FileReader(doc));
        String user;
        String server="";
        String port = "";
        boolean aliasExist = false;
        while ((user = obj.readLine()) != null){
            String[] login = user.split(":");
            if(!(login[0].equals(alias))){
                ecrivain.write(user+"\n");
            }
            if(login[0].equals(alias)){
                aliasExist = true;
                server= login[1];
                port = login[2];
            }
        }
        doc.delete();
        obj.close();
        if(aliasExist){
            ecrivain.write(newAlias + ":" + server + ":"+ port +"\n");  
        }
        ecrivain.close();
        File file = new File("/Users/semaealtinkaynak/Documents/sr2/projet1/src/main/java/sr2_flop_box/manager/data/serveur1.txt");

        File rename = new File("/Users/semaealtinkaynak/Documents/sr2/projet1/src/main/java/sr2_flop_box/manager/data/serveur.txt");

        file.renameTo(rename);
        return aliasExist;

    }

    public String getAllServerAssociation() throws IOException{
        File doc = new File("/Users/semaealtinkaynak/Documents/sr2/projet1/src/main/java/sr2_flop_box/manager/data/serveur.txt");
        BufferedReader obj = new BufferedReader(new FileReader(doc));
        String user;
        JSONArray server = new JSONArray();
        while ((user = obj.readLine()) != null){
            String[] val = user.split(":");
            JSONObject element = new JSONObject();
            element.put("alias", val[0]);
            element.put("server",val[1] );
            element.put("port", val[2]);
            JSONArray links = new JSONArray();
            Link link1 = Link.fromUri("http://localhost:8080/flop-box/{alias}")
                .rel("Request files's names from the server").type("application/json")
                .build(val[0]);
            links.put(new JSONObject().put("href", link1.getUri()).put("rel", link1.getRel()).put("type", link1.getType()));
            element.put("links", links);
            server.put(element);
        }
        
        obj.close();
        return server.toString();
    }


    public boolean changeServerIntoApplication(String alias, String newservername) throws IOException{
        FileWriter ecrivain; 

        ecrivain = new FileWriter("/Users/semaealtinkaynak/Documents/sr2/projet1/src/main/java/sr2_flop_box/manager/data/serveur1.txt"); 

        File doc = new File("/Users/semaealtinkaynak/Documents/sr2/projet1/src/main/java/sr2_flop_box/manager/data/serveur.txt");

        BufferedReader obj = new BufferedReader(new FileReader(doc));
        String user;
        String port = "";
        boolean aliasExist = false;
        while ((user = obj.readLine()) != null){
            String[] login = user.split(":");
            if(!(login[0].equals(alias))){
                ecrivain.write(user+"\n");
            }
            if(login[0].equals(alias)){
                aliasExist = true;
                port = login[2];
            }
        }
        doc.delete();
        obj.close();
        if(aliasExist){
            ecrivain.write(alias + ":" + newservername + ":"+ port +"\n");    
        }
        ecrivain.close();
        File file = new File("/Users/semaealtinkaynak/Documents/sr2/projet1/src/main/java/sr2_flop_box/manager/data/serveur1.txt");

        File rename = new File("/Users/semaealtinkaynak/Documents/sr2/projet1/src/main/java/sr2_flop_box/manager/data/serveur.txt");

        file.renameTo(rename);
        return aliasExist;

    }

    public boolean changeServerPortIntoApplication(String alias, String port) throws IOException{
        FileWriter ecrivain; 

        ecrivain = new FileWriter("/Users/semaealtinkaynak/Documents/sr2/projet1/src/main/java/sr2_flop_box/manager/data/serveur1.txt"); 

        File doc = new File("/Users/semaealtinkaynak/Documents/sr2/projet1/src/main/java/sr2_flop_box/manager/data/serveur.txt");

        BufferedReader obj = new BufferedReader(new FileReader(doc));
        String user;
        String server="";
        boolean aliasExist = false;
        while ((user = obj.readLine()) != null){
            String[] login = user.split(":");
            if(!(login[0].equals(alias))){
                ecrivain.write(user+"\n");
            }
            if(login[0].equals(alias)){
                aliasExist = true;
                server = login[1];
            }
        }
        doc.delete();
        obj.close();
        if(aliasExist){
            ecrivain.write(alias + ":" + server + ":"+ port+"\n");
            
        }
        ecrivain.close();
        File file = new File("/Users/semaealtinkaynak/Documents/sr2/projet1/src/main/java/sr2_flop_box/manager/data/serveur1.txt");

        File rename = new File("/Users/semaealtinkaynak/Documents/sr2/projet1/src/main/java/sr2_flop_box/manager/data/serveur.txt");

        file.renameTo(rename);
        return aliasExist;

    }

}
