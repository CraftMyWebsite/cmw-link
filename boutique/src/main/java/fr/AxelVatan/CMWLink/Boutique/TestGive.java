package fr.AxelVatan.CMWLink.Boutique;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import express.http.request.Request;
import express.http.response.Response;
import fr.AxelVatan.CMWLink.Boutique.Methods.Result;
import fr.AxelVatan.CMWLink.Common.WebServer.CMWLRoute;
import fr.AxelVatan.CMWLink.Common.WebServer.RouteType;
import net.md_5.bungee.api.ProxyServer;

public class TestGive extends CMWLRoute<Main>{

	public TestGive(Main plugin) {
		super(plugin, "give/:username/:item/:qty", RouteType.GET);
	}

	@Override
	public void execute(Request req, Response res) {
		String username = req.getParam("username");
		String item = req.getParam("item");
		int qty = Integer.valueOf(req.getParam("qty"));
		if(this.getPlugin().isInBungee()) {
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("give");
			out.writeUTF(username);
			out.writeUTF(item);
			out.writeInt(qty);
			ProxyServer.getInstance().getPlayer(username).sendData("cmw-link", null);
			/*while() {
				
			}*/
		}else{
			Result result = this.getPlugin().getMethods().runGive(username, item, qty);
			res.send(result.name());
		}
	}
}
