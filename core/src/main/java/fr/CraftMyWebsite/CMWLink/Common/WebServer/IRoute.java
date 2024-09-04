package fr.CraftMyWebsite.CMWLink.Common.WebServer;

import express.http.request.Request;
import express.http.response.Response;

public interface IRoute {

	/**
	 * Get the route name of the package (http:127.0.0.1/PACKAGE_PREFIX/ROUTE_NAME).
	 */
	public abstract String getRouteName();
	
	/**
	 * Get the route type (GET, POST, PUT)
	 */
	public abstract RouteType getRouteType();
	
	/**
	 * Handle the request in package
	 * @param req, see for more information https://github.com/Simonwep/java-express#request-object
	 * @param res, see for more information https://github.com/Simonwep/java-express#response-object
	 */
	public void execute(Request req, Response res);

}
