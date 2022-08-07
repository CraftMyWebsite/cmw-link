package fr.AxelVatan.CMWLink.BungeeCord;

import fr.AxelVatan.CMWLink.Common.Packages.CMWLPackage;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class BG_Commands extends Command{

	private BungeeCordMain main;
	
	public BG_Commands(BungeeCordMain main) {
		super("bcmwl");
		this.main = main;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(args.length == 0) {
			help(sender);
		}else if(args[0].equalsIgnoreCase("packages")) {
			sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&6CMW-Link: &7Packages installés")));
			for(CMWLPackage packageClass : this.main.getConfigFile().getPackages().getPackagesLoaded()) {
				sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&7- &a" + packageClass.getPluginName() + "&7, Versin: &a" + packageClass.getVersion())));
			}
			
		}else {
			help(sender);
		}
	}

	private void help(CommandSender sender) {
		
	}
}
