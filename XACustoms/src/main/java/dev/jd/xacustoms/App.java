package dev.jd.xacustoms;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public class App extends JavaPlugin {

	private XACustomsListener listener;

	@Override
	public void onEnable() {
		listener = new XACustomsListener(this);
		getServer().getPluginManager().registerEvents(listener, this);
		Glow.register();

		addCraftingRecipes();

	}

	@Override
	public void onDisable() {
		listener.onServerShutdown();
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (label.equalsIgnoreCase("ironcarrot")) {
			if (sender.isOp() && sender instanceof Player) {
				((Player) sender).getInventory().addItem(CustomItems.getIronCarrot());
				return true;
			}
		} else if (label.equalsIgnoreCase("currency")) {
			if (sender.isOp() && sender instanceof Player) {
				((Player) sender).getInventory().addItem(CustomItems.getCurrency());
				return true;
			}
		} else if (label.equalsIgnoreCase("diamondcarrot")) {
			if (sender.isOp() && sender instanceof Player) {
				((Player) sender).getInventory().addItem(CustomItems.getDiamondCarrot());
				return true;
			}
		} else if (label.equalsIgnoreCase("emeraldcarrot")) {
			if (sender.isOp() && sender instanceof Player) {
				((Player) sender).getInventory().addItem(CustomItems.getEmeraldCarrot());
				return true;
			}
		}

		return false;
	}

	private void addCraftingRecipes() {
		NamespacedKey diamondCarrotKey = new NamespacedKey(this, "diamond_carrot");
		ShapedRecipe diamondCarrotRecipe = new ShapedRecipe(diamondCarrotKey, CustomItems.getDiamondCarrot());

		diamondCarrotRecipe.shape("DDD", "DID", "DDD");
		diamondCarrotRecipe.setIngredient('D', Material.DIAMOND_BLOCK);
		diamondCarrotRecipe.setIngredient('I', CustomItems.getIronCarrot().getType());

		Bukkit.addRecipe(diamondCarrotRecipe);

		NamespacedKey emeraldCarrotKey = new NamespacedKey(this, "emerald_carrot");
		ShapedRecipe emeraldCarrotRecipe = new ShapedRecipe(emeraldCarrotKey, CustomItems.getEmeraldCarrot());

		emeraldCarrotRecipe.shape("EEE", "EIE", "EEE");
		emeraldCarrotRecipe.setIngredient('E', Material.EMERALD_BLOCK);
		emeraldCarrotRecipe.setIngredient('I', CustomItems.getIronCarrot().getType());

		Bukkit.addRecipe(emeraldCarrotRecipe);
	}

}
