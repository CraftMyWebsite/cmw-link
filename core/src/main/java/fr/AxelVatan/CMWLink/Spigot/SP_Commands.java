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
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7- &a" + packageClass.getPluginName() + "&7, Versin: &a" + packageClass.getVersion()));
			}
			
		}else {
			help(sender);
		}
		return true;
	}

	private void help(CommandSender sender) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6CMW-Link: &7List des commandes"));
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7- cmwl &apackages | &7Affiche les packages actifs"));
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7- cmwl &apackages | &7Recharge le plugin"));
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (args.length > 2 || args.length == 0){
            return null;
        }
		List<String> matches = new ArrayList<String>();
		if(args.length == 1) {
			matches.add("packages");
			matches.add("reload");
		}
		return matches;
	}

}
