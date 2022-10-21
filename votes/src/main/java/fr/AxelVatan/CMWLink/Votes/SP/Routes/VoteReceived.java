package fr.AxelVatan.CMWLink.Votes.SP.Routes;

import java.util.HashMap;

import fr.AxelVatan.CMWLink.Common.WebServer.CMWLRoute;
import fr.AxelVatan.CMWLink.Common.WebServer.RouteType;
import fr.AxelVatan.CMWLink.Votes.SP.Main;

public class VoteReceived extends CMWLRoute<Main>{
	
	//AFFICHER UN MESSAGE EN JEUX CONFIGURABLE WITH PLACEHOLDER
	public VoteReceived(Main main) {
		super(main, "send/:username/:site_id/:reward_id", RouteType.GET);
	}

	@Override
	public String executeRoute(HashMap<String, String> params) {
		return null;
	}

}
