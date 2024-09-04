package fr.CraftMyWebsite.CMWLink.Shop.BG;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.logging.Level;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import fr.CraftMyWebsite.CMWLink.Shop.BG.Routes.TestGive;
import fr.CraftMyWebsite.CMWLink.Common.Packages.CMWLPackage;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class Main extends CMWLPackage implements Listener{

	private Map<String, Deque<BiConsumer<ProxiedPlayer, byte[]>>> callbacks;

	@Override
	public void enable() {
		this.log(Level.INFO, "Shop for BungeeCord enabled.");
		this.callbacks = new HashMap<String, Deque<BiConsumer<ProxiedPlayer, byte[]>>>();
		ProxyServer.getInstance().registerChannel("cmw:shop");
		ProxyServer.getInstance().getPluginManager().registerListener(ProxyServer.getInstance().getPluginManager().getPlugin("CraftMyWebsite_Link"), this);
	}

	@Override
	public void disable() {

	}

	@Override
	public void registerRoutes() {
		this.addRoute(new TestGive(this));
	}

	@EventHandler
	public void on(PluginMessageEvent event) {
		if (!event.getTag().equalsIgnoreCase("cmw:shop")){
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput( event.getData() );
        String subChannel = in.readUTF();
        consume(subChannel, (ProxiedPlayer) event.getReceiver(), event.getData());
	}
	
	public void request(ProxiedPlayer target, String bungeeSubChannel, BiConsumer<ProxiedPlayer, byte[]> callback, String username, String item, int qty) {
		callbacks.computeIfAbsent(bungeeSubChannel, key -> new ArrayDeque<>()).add(callback);
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF(bungeeSubChannel);
		out.writeUTF(username);
		out.writeUTF(item);
		out.writeInt(qty);
		target.getServer().sendData("cmw:shop", out.toByteArray());
	}

	protected void consume(String bungeeSubChannel, ProxiedPlayer receiver, byte[] message) {
		Deque<BiConsumer<ProxiedPlayer, byte[]>> callbackQueue = callbacks.computeIfAbsent(bungeeSubChannel, key -> new ArrayDeque<>());
		if (!callbackQueue.isEmpty()) {
			callbackQueue.poll().accept(receiver, message);
		}
	}

}
