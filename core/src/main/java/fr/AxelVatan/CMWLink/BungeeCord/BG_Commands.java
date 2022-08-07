package fr.AxelVatan.CMWLink.BungeeCord;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import fr.AxelVatan.CMWLink.Common.Packages.CMWLPackage;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class BG_Commands extends Command implements TabExecutor{

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
		sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&6CMW-Link: &7List des commandes")));
		sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&7- bcmwl &apackages |&7Affiche les packages actifs")));
		sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&7- bcmwl &apackages |&7Recharge le plugin")));
	}

	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
		if (args.length > 2 || args.length == 0){
            return ImmutableSet.of();
        }
		Set<String> matches = new HashSet<String>();
		if(args.length == 1) {
			matches.add("packages");
			matches.add("reload");
		}
		return matches;
	}
}
