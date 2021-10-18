package dev.jd.xacustoms;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;

public class CustomItems {

	public static ItemStack getIronCarrot() {
		ItemStack ironCarrot = new ItemStack(Material.GOLDEN_CARROT, 1);
		NBTItem nbti = new NBTItem(ironCarrot);

		nbti.setInteger("CustomModelData", 8280001);

		NBTCompound display = nbti.addCompound("display");
		display.setString("Name", "{\"text\":\"Iron Carrot\",\"italic\":\"false\"}");
		return nbti.getItem();
	}

}
