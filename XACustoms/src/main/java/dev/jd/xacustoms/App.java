package dev.jd.xacustoms;

import org.bukkit.plugin.java.JavaPlugin;

public class App extends JavaPlugin {

	private XACustomsListener listener;

	@Override
	public void onEnable() {
		listener = new XACustomsListener();
		getServer().getPluginManager().registerEvents(listener, this);
	}

	@Override
	public void onDisable() {

	}

}
