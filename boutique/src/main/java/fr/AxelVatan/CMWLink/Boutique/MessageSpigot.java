package fr.AxelVatan.CMWLink.Boutique;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

public class MessageSpigot implements PluginMessageListener {

	private Main main;
	
	public MessageSpigot(Main main) {
		this.main = main;
	}
	
	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (!channel.equals("cmw-link")) {
			return;
		}
		ByteArrayDataInput in = ByteStreams.newDataInput(message);
	    String subchannel = in.readUTF();
	    if (subchannel.equals("give")) {
	    	String username = in.readUTF();
	    	String item = in.readUTF();
	    	int qty = in.readInt();
	    	this.main.getMethods().runGive(username, item, qty);
	    	System.out.println("IT'S WORK");
	    }
	}

}
