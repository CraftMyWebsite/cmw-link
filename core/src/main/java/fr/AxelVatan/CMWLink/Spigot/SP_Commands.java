package fr.AxelVatan.CMWLink.Spigot;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import fr.AxelVatan.CMWLink.Common.Packages.CMWLPackage;

public class SP_Commands implements CommandExecutor, TabCompleter {

	private SpigotMain main;
	
	public SP_Commands(SpigotMain main) {
		this.main = main;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length == 0) {
			help(sender);
		}else if(args[0].equalsIgnoreCase("packages")) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6CMW-Link: &7Packages installés"));
			for(CMWLPackage packageClass : this.main.getConfigFile().getPackages().getPackagesLoaded()) {
				boolean certified = main.getConfigFile().getPackages().getPackagesCertified().containsKey(packageClass.getPluginName());
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7- &a" + packageClass.getPluginName() + "&7, Version: &a" + packageClass.getVersion() + " &8| " + (certified ? "&bCertifié [CMW]" : "&4Non certifié [CMW]")));
			}
		}else if(args[0].equalsIgnoreCase("reload")) {
			if(sender.hasPermission("cmwl.reload")) {
				this.main.getConfigFile().getWebServer().disable();
				this.main.getConfigFile().getPackages().disablePackages();
				this.main.resetConfig();
			}else {
				errorPerm(sender);
			}
		}else {
			help(sender);
		}
		return true;
	}

	private void help(CommandSender sender) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6CMW-Link: &7Liste des commandes"));
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7- cmwl &apackages &8| &7Affiche les packages actifs"));
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7- cmwl &areload &8| &7Recharge le plugin"));
	}

	private void errorPerm(CommandSender sender) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Vous n'avez pas la permission de faire cette commande !"));
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (args.length > 2 || args.length == 0){
            return null;
        }
		List<String> matches = new ArrayList<String>();
		if(args.length == 1) {
			if(sender.hasPermission("cmwl.packages")) {
				matches.add("packages");
			}
			if(sender.hasPermission("cmwl.reload")) {
				matches.add("reload");
			}
		}
		return matches;
	}

}
