# CraftMyWebsite-link
CraftMyWebsite-Link a java plugin for MC servers


### CompatibilitÃ©
------------
- **Spigot/Paper**
- **BungeeCord/Waterfall**
- **Velocity**

### Installation
------------

```json
{
 	"port": 24102,
  	"logRequests": true,
 	"useProxy": false
}

```

- Port: Port sur lequel l'api du plugin est accessible.
- logRequests: Affiche-les demande de requêtes dans la console du serveur.
- useProxy: Permet d'utilise les Proxy tel que BungeeCord, Waterfall, Velocity.

Pour vérifié le bon fonctionnement du plugin il suffit de taper l'IP du serveur avec le port dans un navigateur, le plugin envoie la réponse suivante:

```
URL: http://127.0.0.1:24102/
Réponse: {"CODE":200,"NAME":"CraftMyWebSite_Link","VERSION":1.0}
```

### Pour les développeurs
------------
Code de base pour un package.


Class TestPackage:

```java

package fr.AxelVatan.CMWLink.TestPackages;

import java.util.logging.Level;
import fr.AxelVatan.CMWLink.Common.Packages.CMWLPackage;

public class TestPackage extends CMWLPackage{

	@Override
	public void enable() {
		this.log(Level.INFO, "TestPackage enabled.");
	}

	@Override
	public void disable() {
		
	}

	@Override
	public void registerRoutes() {
		this.addRoute(new TestRoute(this));
	}
}

```

- Les fonctions enable, disable et registerRoutes sont obligatoire, elle sont appeler lors du chargement/arrêt du package.

<br>
Class TestRoute:

```java
package fr.AxelVatan.CMWLink.TestPackages;

import express.http.request.Request;
import express.http.response.Response;
import fr.AxelVatan.CMWLink.Common.WebServer.CMWLRoute;
import fr.AxelVatan.CMWLink.Common.WebServer.RouteType;

public class TestRoute extends CMWLRoute<TestPackage>{

	public TestRoute(TestPackage main) {
		super(main, "test", "ping", RouteType.GET);
	}

	@Override
	public void execute(Request req, Response res) {
		System.out.print("Pong !");
		res.send("Pong !");
	}

}


```

Dans le constructeur vous devez obligatoirement déclaré les parametres suivant:
- main : La class principale du package
- "test" : Le prefix de la route
- "ping" : Le nom de la route
- RouteType: Le type de route (GET, POST)

La fonction "execute" est déclanchée quand la route définie est appelée.

- http://127.0.0.1/test/ping -> Réponse: Pong !

<br>
Pour que le package soit reconnue par le plugin vous devez avoir un package.yml dans le jar

```yaml
name: CMWL_Votes
main: fr.AxelVatan.CMWLink.TestPackages.TestPackage
version: 1.0
author: AxelVatan

```

- name: Le nom du package.
- main: Le chemin vers la class principale du package.
- verison: La version du package.
- author: L'auteur du package.

### Support, infos et communautÃ©
------------

### Liens utiles :
- **Discord**: https://discord.gg/tscRZCU
- **Forum**: https://craftmywebsite.fr/forum
- **Contact**: https://craftmywebsite.fr/contactez/nous
- **Twitter**: https://twitter.com/CraftMyWebsite

------------
Copyright Â© CraftMyWebsite 2014-2022 