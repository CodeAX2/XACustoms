package dev.jd.xacustoms;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import net.md_5.bungee.api.ChatColor;

public class CustomItems {

	public static ItemStack getIronCarrot() {
		ItemStack ironCarrot = new ItemStack(Material.GOLDEN_CARROT, 1);
		NBTItem nbti = new NBTItem(ironCarrot);

		nbti.setInteger("CustomModelData", 8280001);

		NBTCompound display = nbti.addCompound("display");
		display.setString("Name", "{\"text\":\"Iron Carrot\",\"italic\":\"false\"}");
		return nbti.getItem();
	}

	public static ItemStack getCurrency() {
		ItemStack currency = new ItemStack(Material.WHEAT_SEEDS, 1);

		ItemMeta currencyMeta = currency.getItemMeta();
		currencyMeta.setLore(Arrays.asList(ChatColor.GOLD + "This is used as the official currency.",
				ChatColor.GOLD + "Obtained as you play the game.", "", ChatColor.BLUE + "Make sure to have server",
				ChatColor.BLUE + "resource packs enabled."));
		currency.setItemMeta(currencyMeta);

		currency.addEnchantment(Glow.getEnchantment(), 1);

		NBTItem nbti = new NBTItem(currency);

		nbti.setInteger("CustomModelData", 8280002);

		NBTCompound display = nbti.addCompound("display");
		display.setString("Name", "{\"text\":\"Dalir\",\"italic\":\"false\",\"color\":\"green\"}");

		return nbti.getItem();
	}

	public static ItemStack getDiamondCarrot() {
		ItemStack diamondCarrot = new ItemStack(Material.GOLDEN_CARROT, 1);

		diamondCarrot.addEnchantment(Glow.getEnchantment(), 1);

		NBTItem nbti = new NBTItem(diamondCarrot);

		nbti.setInteger("CustomModelData", 8280003);

		NBTCompound display = nbti.addCompound("display");
		display.setString("Name", "{\"text\":\"Diamond Carrot\",\"italic\":\"false\"}");
		return nbti.getItem();
	}

	public static ItemStack getEmeraldCarrot() {
		ItemStack emeraldCarrot = new ItemStack(Material.GOLDEN_CARROT, 1);

		emeraldCarrot.addEnchantment(Glow.getEnchantment(), 1);

		NBTItem nbti = new NBTItem(emeraldCarrot);

		nbti.setInteger("CustomModelData", 8280004);

		NBTCompound display = nbti.addCompound("display");
		display.setString("Name", "{\"text\":\"Emerald Carrot\",\"italic\":\"false\",\"color\":\"green\"}");
		return nbti.getItem();
	}


}
