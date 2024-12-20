package fr.CraftMyWebsite.CMWLink.Velocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;

import fr.CraftMyWebsite.CMWLink.Common.Packages.CMWLPackage;
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
			if(invocation.source().hasPermission("cmwl.packages")) {
				invocation.source().sendMessage(Component.text("CMW-Link: ", NamedTextColor.GOLD).append(Component.text("Packages installés", NamedTextColor.GRAY)));
				for(CMWLPackage packageClass : this.main.getConfigFile().getPackages().getPackagesLoaded()) {
					boolean certified = main.getConfigFile().getPackages().getPackagesCertified().containsKey(packageClass.getPackageName());
					Component certifiedText = Component.text("Certifié [CMW]", NamedTextColor.AQUA);
					Component unCertifiedText = Component.text("Non certifié [CMW]", NamedTextColor.DARK_RED);
					invocation.source().sendMessage(Component.text("- ", NamedTextColor.GRAY).append(Component.text(packageClass.getPackageName(), NamedTextColor.GREEN).append(Component.text(", Version: ", NamedTextColor.GRAY).append(Component.text(packageClass.getVersion(), NamedTextColor.GREEN).append(Component.text(" | ", NamedTextColor.DARK_GRAY).append((certified ? certifiedText : unCertifiedText)))))));
				}
			}else {
				errorPerm(invocation.source());
			}
		}else if(invocation.arguments()[0].equalsIgnoreCase("reload")) {
			if(invocation.source().hasPermission("cmwl.reload")) {
				this.main.getConfigFile().getWebServer().disable();
				this.main.getConfigFile().getPackages().disablePackages();
				this.main.resetConfig();
			}else {
				errorPerm(invocation.source());
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
	
	private void errorPerm(CommandSource sender) {
		sender.sendMessage(Component.text("Vous n'avez pas la permission de faire cette commande !", NamedTextColor.DARK_RED));
	}
}