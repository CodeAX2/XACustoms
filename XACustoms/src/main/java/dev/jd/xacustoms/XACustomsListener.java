package dev.jd.xacustoms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class XACustomsListener implements Listener {

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {

		if (e.getBlock().getType() == Material.CARROTS) {
			Collection<ItemStack> itemDroppedCollection = e.getBlock()
					.getDrops(e.getPlayer().getInventory().getItemInMainHand(), e.getPlayer());

			List<ItemStack> itemsDropped = new ArrayList<ItemStack>(itemDroppedCollection);

			Random r = new Random();

			boolean shouldDropIronCarrot = r.nextDouble() < 0.01 && itemsDropped.size() > 1;
			if (shouldDropIronCarrot) {
				e.setCancelled(true);
				itemsDropped.get(1).setAmount(itemsDropped.get(1).getAmount() - 1);
				itemsDropped.add(CustomItems.getIronCarrot());
				e.getBlock().setType(Material.AIR);
				e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), CustomItems.getIronCarrot());
			}

		}

	}

}
