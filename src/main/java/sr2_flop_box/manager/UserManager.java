package sr2_flop_box.manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class UserManager {
    
    public UserManager(){

    }

    public boolean createUserIntoApplication(String name, String passeword) throws IOException{
        File doc = new File("/Users/semaealtinkaynak/Documents/sr2/projet1/src/main/java/sr2_flop_box/manager/data/utilisateur.txt");

        BufferedReader obj = new BufferedReader(new FileReader(doc));
        boolean in=true;
        String user;
        while ((user = obj.readLine()) != null){
            String[] login = user.split(":");
            if(login[0].equals(name)){
                in = false;
            }
        }
        if(in){
            FileWriter fichier = new FileWriter("/Users/semaealtinkaynak/Documents/sr2/projet1/src/main/java/sr2_flop_box/manager/data/utilisateur.txt",true);
            fichier.write(name +":"+passeword + "\n");
            fichier.close();
        }
        obj.close();
        return in;
    }

    public boolean deleteUserIntoApplication(String name, String passeword) throws IOException{
        FileWriter ecrivain; 

        ecrivain = new FileWriter("/Users/semaealtinkaynak/Documents/sr2/projet1/src/main/java/sr2_flop_box/manager/data/utilisateur1.txt"); 

        File doc = new File("/Users/semaealtinkaynak/Documents/sr2/projet1/src/main/java/sr2_flop_box/manager/data/utilisateur.txt");

        BufferedReader obj = new BufferedReader(new FileReader(doc));
        String user;
        while ((user = obj.readLine()) != null){
            String[] login = user.split(":");
            if(!(login[0].equals(name))){
                ecrivain.write(user+"\n");
            }
        }
        doc.delete();
        ecrivain.close();
        File file = new File("/Users/semaealtinkaynak/Documents/sr2/projet1/src/main/java/sr2_flop_box/manager/data/utilisateur1.txt");

        File rename = new File("/Users/semaealtinkaynak/Documents/sr2/projet1/src/main/java/sr2_flop_box/manager/data/utilisateur.txt");
        obj.close();
        boolean flag = file.renameTo(rename);
        return flag;

    }

    public boolean checkUserExistAndPasswordIsCorrect(String name, String password) throws IOException{
        File doc = new File("/Users/semaealtinkaynak/Documents/sr2/projet1/src/main/java/sr2_flop_box/manager/data/utilisateur.txt");

        BufferedReader obj = new BufferedReader(new FileReader(doc));
        boolean in=false;
        String user;
        while ((user = obj.readLine()) != null){
            String[] login = user.split(":");
            if(login[0].equals(name)  && login[login.length -1].equals(password) ){
                in = true;
            }
        }
        obj.close();
        return in;
    }

 

 
}
