package fr.AxelVatan.CMWLink.Boutique.BG.Routes;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import express.http.request.Request;
import express.http.response.Response;
import fr.AxelVatan.CMWLink.Boutique.BG.Main;
import fr.AxelVatan.CMWLink.Common.WebServer.CMWLRoute;
import fr.AxelVatan.CMWLink.Common.WebServer.RouteType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class TestGive extends CMWLRoute<Main>{

	public TestGive(Main plugin) {
		super(plugin, "give/:username/:item/:qty", RouteType.GET);
	}

	@Override
	public void execute(Request req, Response res) {
		String username = req.getParam("username");
		String item = req.getParam("item");
		int qty = Integer.valueOf(req.getParam("qty"));
		ProxiedPlayer sender = ProxyServer.getInstance().getPlayer(username);
		this.getPlugin().request(sender, "give", (rec, msg) -> {
			ByteArrayDataInput in = ByteStreams.newDataInput(msg);
			res.send("Success " + in.readUTF() + " response: " + in.readUTF());
		}, username, item, qty);
	}
}
