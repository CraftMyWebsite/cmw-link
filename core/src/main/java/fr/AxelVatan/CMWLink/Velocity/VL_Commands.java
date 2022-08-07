package fr.AxelVatan.CMWLink.Velocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;

import fr.AxelVatan.CMWLink.Common.Packages.CMWLPackage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class VL_Commands implements SimpleCommand {

	private VelocityMain main;
	
	public VL_Commands(VelocityMain main) {
		this.main = main;
	}

	@Override
	public void execute(Invocation invocation) {
		if(invocation.arguments().length == 0) {
			help(invocation.source());
		}else if(invocation.arguments()[0].equalsIgnoreCase("packages")) {
			invocation.source().sendMessage(Component.text("CMW-Link: ", NamedTextColor.GOLD).append(Component.text("Packages installés", NamedTextColor.GRAY)));
			for(CMWLPackage packageClass : this.main.getConfigFile().getPackages().getPackagesLoaded()) {
				invocation.source().sendMessage(Component.text("- ", NamedTextColor.GRAY).append(Component.text(packageClass.getPluginName(), NamedTextColor.GREEN).append(Component.text(", Version: ", NamedTextColor.GRAY).append(Component.text(packageClass.getVersion(), NamedTextColor.GREEN)))));
			}
			
		}else {
			help(invocation.source());
		}
	}

	private void help(CommandSource sender) {
		sender.sendMessage(Component.text("CMW-Link: ", NamedTextColor.GOLD).append(Component.text("Liste des commandes", NamedTextColor.GRAY)));
		sender.sendMessage(Component.text("- vcmwl ", NamedTextColor.GRAY).append(Component.text("packages", NamedTextColor.GREEN).append(Component.text(" | ", NamedTextColor.DARK_GRAY).append(Component.text("Affiche les packages actifs", NamedTextColor.GRAY)))));
		sender.sendMessage(Component.text("- vcmwl ", NamedTextColor.GRAY).append(Component.text("reload", NamedTextColor.GREEN).append(Component.text(" | ", NamedTextColor.DARK_GRAY).append(Component.text("Recharge le plugin", NamedTextColor.GRAY)))));
	}
}
