# SR2-flop-box-altinkaynak

Simulation d'une API REST en Java <br>
Semae altinkaynak<br>
20/03/22<br>

# Introduction

L'objectif du projet était de réaliser une application nommé flopbox qui permettrait de communiquer avec plusieurs serveurs FTP. L'application serait une API qui a le style architectural REST. Le code utilise la bibliothéque commons.net d'Apache et le projet a été génèré à partir de l'archetype Maven jersey-quickstart-grizzly2. <br>

# Architecture 

L'application utilise des fichiers textes pour stockées les serveurs ftp et les utilisateurs de l'application. Le code possède 3 paquetages: 

## Paquetage authenticate

Ce paquetage contient un filter qui permet d'intercepeter le nom et le mot de passe de l'utilisateur vis à vis de l'application. Pour cela, il a fallu créer une interface permettant de faire le bind entre les classes et le filtre. Voici l'interface en question : 

```java

@NameBinding
@Target({ ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Authentification {
    
}
```

et voici le code de la fonction de filtre :

```java
@Authentification
@Provider
public class RequestFilter implements ContainerRequestFilter{

    private UserManager manager = new UserManager();

    
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // Si l'utilisateur n'existe pas dans la fausse base de données alors un erreur de type 401 est envoyés
        if(!(this.manager.checkUserExistAndPasswordIsCorrect(requestContext.getHeaderString("login"), requestContext.getHeaderString("password")))){
            requestContext.abortWith((Response.status(Response.Status.UNAUTHORIZED).entity("the user doesn't exist or the password is uncorrect, please suscribe into the application or write the correct password!!").build()));
        }
        
    }
    
}
```

L'annotation permet d'automatiser la vérification de l'utilisateur dans l'application. Cela allège grandement le code. 


## Paquetage manager

Ce paquegate contient la gestion des données de l'application et la communication avec les serveurs. Il utilise les fichiers serveur.txt et les fichiers utilisateur.txt . <br>

La classe UserManager contient pour chaque fonction une exception de type **throws IOException.class**. Cela est du au faite que les fonctions lisent et écrivent sur le fichier  **utilisateur.txt**. <br>

La classe ServerManager contient, aussi, des **throws IOException** pour gèrer les cas d'erreur en cas de mauvais lecture et ecriture sur le fichier serveur.txt<br>

La classe FTPClientConnect utilise la bibliothéque commons.net d'Apache. Elle possède beaucoup de fonctions pour la communication avec les serveurs. Ainsi les exceptions permettent d'envoyer des erreurs de type 500 lors d'une mauvaise manipulation avec les serveurs. Voici un exemple de code permettant de gèrer le detection d'erreur: <br>

```java

 public void connectToFTPServer() throws Exception{
        this.ftpClient.connect(this.server, this.port);
        int reply = ftpClient.getReplyCode();
        if(!FTPReply.isPositiveCompletion(reply)) {
            ftpClient.disconnect();
            throw new Exception("FTP server refused connection.");
          }
    }
```


## Paquetage flopbox

Ce paquetage contient les fonctions nécessaires pour l'execution des requetes des utilisateurs. <br>

Ainsi la classe User contient les fonctions necéssaire pour la gestion des utilisateurs de l'application. Elle contient une méthode d'ajout et de suppresion d'utilisateur. De plus chaque fonctions contient un **throws IOException.class**. <br>

La classe ServerManager contient toutes les fonctions permettant de répondre aux requetes concernant la gestion des serveurs et leur alias dans le fichier serveur.txt. La classe est dépendante de la classe ServerManager, cela permet de répartir le code dans plusieurs classe et le rendre plus agréable à lire. <br>

La classe Client permet de gèrer les requetes faites pour les serveurs. Pour être honnête, il aurait surement été préferable de faire du clean code. Voici la première fonction permettant de récuperer les dossiers et fichiers racines du serveur :<br>

```java

    @Path("/{alias}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllFolderName(@HeaderParam("ftp-login") String name, @HeaderParam("ftp-password") String pass,@PathParam("alias") String alias) throws Exception{
        // verification de la présence de l'alias
        String serverContent = servers.getServerAssociation(alias);
        if(serverContent!=""){
            String[] elements = serverContent.split(":");
            // connexion au serveur
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
```

Nous pouvons donc voir que l'application vérifie que l'alias existe bien et se connecte au serveur associé puis renvoi un code de réponse avec le json associé. <br>

Les fonctions possèdent l'exception **NumberFormatException** car cela permet de gènerer un cas d'erreur au cas ou le port indiqué en cas de connexion ne serait pas un nombre. <br>


# Design pattern et architecture

Le design pattern mise en place serait 'Chain of Responsibility', c'est à dire que il y a une dèlegation de la charge de travail et de responsabilité entre les classes. Le meilleur exemple serait le filtre d'authentification qui se comporte comme un handler. Nous avons vu plus haut les classes et les fonctions impliquées, mais voici les diffèrentes classes impliquées: <br>
<ul>
<li>Authentification</li>
<li>RequestFilter</li>
<li>ServerManager</li>
<li>FtpClientConnect</li>
</ul>

De plus, il serait peut-être possible de mettre en place le design pattern decorateur pour ne pas surchargées les classes lors de l'evolution de la plateforme. <br>


# Optimisation

Pour être honnête, les classes du paquetages manager pourraient être grandement optimisésor le manque de temps ne le permet pas... 
La classe FTPClientConnect possède du code répeter, il aurait fallu optimoser cette partie du code <br>

# Conclusion

Globalement l'application fonctionne, la seule chose qui n'est pas mise en place est le mode de connexion passive ou active. Le cahier des charges est dument respecté. <br>


# Commandes

Le dossier docs contient les sources de la javadoc.
Pour pourvoir tester le serveur, il vous suffit de suivre les commandes ci-dessous: 
- Ouvrez un terminal de commande et placer vous dans le répertoire du projet, normalement vous verrez un dossier src et un fichier pom.xml, puis ecriver la commande suivante **mvn package**. Vous venez de gènerer le jar et les fichiers executables dans le répertoire target
- Il vous faudra toujours rester dans le répertoire du projet et puis ecrire la commande qui suit dans un terminal de commande : 
**java -jar target/projet1-flopbox.jar**. Il est possible que le build renvoie des erreurs lors de la compilation, cela est du au faite que les tests ont été faites avec le serveur webtp personnelle de la fac. Il est toujours possible de commenter les classes de tests et puis de lancer la commande pour gènerer le jar. 
Pour pouvoir generer la javadoc, il vous suffira de vous placer dans le répertoire du projet et de taper la commande **mvn javadoc:javadoc**, cela créera un dossier target qui contiendra un dossier docs. 

