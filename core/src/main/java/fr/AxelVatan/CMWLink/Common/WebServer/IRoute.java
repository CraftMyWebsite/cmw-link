package fr.AxelVatan.CMWLink.Common.WebServer;

import express.http.request.Request;
import express.http.response.Response;

public interface IRoute {

	public abstract String getPackagePrefix();
	
	public abstract String getRouteName();
	
	public abstract RouteType getRouteType();
	
	public void execute(Request req, Response res);

}
