# CraftMyWebsite-link
CraftMyWebsite-Link a java plugin for MC servers


### Compatibilité
------------
- **Spigot/Paper**
- **BungeeCord/Waterfall**
- **Velocity**

### Installation
------------

```json
{
 	"port": 24102,
  	"bindToDefaultPort": true,
  	"loadUncertifiedPackages": true,
  	"logRequests": true,
  	"useProxy": false,
  	"username": "admin",
  	"password": "changeme"
}

```

- port: Port sur lequel l'api du plugin est accessible.
- bindToDefaultPort: Permet d'utiliser le port par défaut de minecraft pour les communications (Paper/Spigot seulement pour le moment)
- loadUncertifiedPackage: Permet de chargé les packages non certifié par l'équipe de CMW
- logRequests: Affiche-les demande de requêtes dans la console du serveur.
- useProxy: Permet d'utilise les Proxy tel que BungeeCord, Waterfall, Velocity.
- username: Le nom de l'utilisateur pour les requettes
- password: Le mot de passe de l'utilisateur pour les requettes

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
		super(main, "ping", RouteType.GET);
	}

	@Override
	public String executeRoute(HashMap<String, String> params) {
		System.out.print("Pong !");
		JsonBuilder json = new JsonBuilder();
		json.append("CODE", 200);
		json.append("MESSAGE", "Pong !");
		return json.build();
	}

}


```

Dans le constructeur vous devez obligatoirement déclaré les parametres suivant:
- main : La class principale du package
- "ping" : Le nom de la route
- RouteType: Le type de route (GET, POST)

La fonction "execute" est déclanchée quand la route définie est appelée.

```
http://127.0.0.1:24102/test/ping -> Réponse: {"CODE":200,"MESSAGE":"Pong !"}
```

<br>
Pour que le package soit reconnue par le plugin vous devez avoir un package.yml dans le jar

```yaml
name: CMWL_Test
route_prefix: test
sp_main: fr.AxelVatan.CMWLink.TestPackages.TestPackage
version: 1.0
author: AxelVatan

```

- name: Le nom du package.
- route_prefix: le prefix pour toutes les route du package.
- sp_main: Le chemin vers la class principale du package pour l'utilisation sur Paper/Spigot.
- bg_main: Le chemin vers la class principale du package pour l'utilisation sur BungeeCord/Waterfall.
- vl_main: Le chemin vers la class principale du package pour l'utilisation sur Velocity.
- version: La version du package.
- author: L'auteur du package.

### Support, infos et communauté
------------

### Liens utiles :
- **Discord**: https://discord.gg/tscRZCU
- **Forum**: https://craftmywebsite.fr/forum
- **Contact**: https://craftmywebsite.fr/contactez/nous
- **Twitter**: https://twitter.com/CraftMyWebsite

------------
Copyright © CraftMyWebsite 2014-2022 