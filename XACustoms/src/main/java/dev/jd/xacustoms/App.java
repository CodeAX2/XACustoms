package dev.jd.xacustoms;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class App extends JavaPlugin {

	private XACustomsListener listener;

	@Override
	public void onEnable() {
		listener = new XACustomsListener(this);
		getServer().getPluginManager().registerEvents(listener, this);
	}

	@Override
	public void onDisable() {

	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (label.equalsIgnoreCase("ironcarrot")) {
			if (sender.isOp() && sender instanceof Player) {
				((Player) sender).getInventory().addItem(CustomItems.getIronCarrot());
			}
		}

		return false;
	}

}
