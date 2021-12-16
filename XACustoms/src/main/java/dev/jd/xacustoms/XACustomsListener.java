package dev.jd.xacustoms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent.Cause;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;

public class XACustomsListener implements Listener {

	private App plugin;

	private List<UUID> visualFirePlayers;
	private Map<UUID, Integer> diamondCarrotPlayers;

	private Team emeraldTeam;

	public XACustomsListener(App plugin) {
		this.plugin = plugin;
		visualFirePlayers = new ArrayList<>();
		diamondCarrotPlayers = new HashMap<>();

		emeraldTeam = Bukkit.getScoreboardManager().getMainScoreboard().getTeam("emeraldTeam");
		if (emeraldTeam == null)
			emeraldTeam = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam("emeraldTeam");
		emeraldTeam.setColor(ChatColor.GREEN);

	}

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

	@EventHandler
	public void onEatFood(PlayerItemConsumeEvent e) {
		if (e.getItem().isSimilar(CustomItems.getIronCarrot())) {
			Random r = new Random();
			if (r.nextDouble() < 0.5) {
				e.getPlayer().setVisualFire(true);
				visualFirePlayers.add(e.getPlayer().getUniqueId());

				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
					Bukkit.getPlayer(e.getPlayer().getUniqueId()).setVisualFire(false);
					visualFirePlayers.remove(e.getPlayer().getUniqueId());
				}, 20 * 60);

			} else {
				e.getPlayer().setFireTicks(20 * 60);
			}
		} else if (e.getItem().isSimilar(CustomItems.getDiamondCarrot())) {
			e.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(10);
			e.getPlayer().setHealth(Math.min(e.getPlayer().getHealth(), 10));
			e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 60, 2));
			e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 60, 4));
			e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 60, 4));
			e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 20 * 60, 1));

			int schedulerTaskId = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
				if (diamondCarrotPlayers.keySet().contains(e.getPlayer().getUniqueId())) {
					Player p = Bukkit.getPlayer(e.getPlayer().getUniqueId());
					AttributeInstance attr = p.getAttribute(Attribute.GENERIC_MAX_HEALTH);
					attr.setBaseValue(attr.getDefaultValue());
					diamondCarrotPlayers.remove(e.getPlayer().getUniqueId());
				}
			}, 20 * 60);

			if (diamondCarrotPlayers.keySet().contains(e.getPlayer().getUniqueId())) {
				Bukkit.getScheduler().cancelTask(diamondCarrotPlayers.get(e.getPlayer().getUniqueId()));
			}
			diamondCarrotPlayers.put(e.getPlayer().getUniqueId(), schedulerTaskId);
		} else if (e.getItem().isSimilar(CustomItems.getEmeraldCarrot())) {
			e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 60, 29));
			e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20 * 60, 29));
			e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20 * 60, 29));
			if (!emeraldTeam.getEntries().contains(e.getPlayer().getName())) {
				emeraldTeam.addEntry(e.getPlayer().getName());
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		e.getPlayer().setVisualFire(false);
		AttributeInstance attr = e.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH);
		attr.setBaseValue(attr.getDefaultValue());
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		if (visualFirePlayers.contains(e.getPlayer().getUniqueId())) {
			e.getPlayer().setVisualFire(true);
		} else {
			e.getPlayer().setVisualFire(false);
		}

		if (diamondCarrotPlayers.containsKey(e.getPlayer().getUniqueId())) {
			AttributeInstance attr = e.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH);
			attr.setBaseValue(10);
		} else {
			AttributeInstance attr = e.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH);
			attr.setBaseValue(attr.getDefaultValue());
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if (diamondCarrotPlayers.keySet().contains(e.getEntity().getUniqueId())) {
			AttributeInstance attr = e.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH);
			attr.setBaseValue(attr.getDefaultValue());
			Bukkit.getScheduler().cancelTask(diamondCarrotPlayers.get(e.getEntity().getUniqueId()));
			diamondCarrotPlayers.remove(e.getEntity().getUniqueId());
		}
		if (emeraldTeam.getEntries().contains(e.getEntity().getName())) {
			emeraldTeam.removeEntry(e.getEntity().getName());
		}
	}

	@EventHandler
	public void onEffectExpire(EntityPotionEffectEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if (e.getCause() == Cause.EXPIRATION || e.getCause() == Cause.MILK) {
				if (e.getOldEffect().getType().equals(PotionEffectType.GLOWING)
						&& emeraldTeam.getEntries().contains(p.getName())) {
					emeraldTeam.removeEntry(p.getName());
				}
			}
		}

	}

	@EventHandler
	public void onCraft(PrepareItemCraftEvent e) {
		if (e.getInventory().getResult() == null)
			return;
		if (e.getInventory().getResult().isSimilar(CustomItems.getDiamondCarrot())) {
			if (!e.getInventory().getMatrix()[4].isSimilar(CustomItems.getIronCarrot())) {
				e.getInventory().setResult(new ItemStack(Material.AIR));
			}
		} else if (e.getInventory().getResult().isSimilar(CustomItems.getEmeraldCarrot())) {
			if (!e.getInventory().getMatrix()[4].isSimilar(CustomItems.getIronCarrot())) {
				e.getInventory().setResult(new ItemStack(Material.AIR));
			}
		}
	}

	public void onServerShutdown() {
		for (UUID pid : visualFirePlayers) {
			Bukkit.getPlayer(pid).setVisualFire(false);
		}

		for (UUID pid : diamondCarrotPlayers.keySet()) {
			Player p = Bukkit.getPlayer(pid);
			AttributeInstance attr = p.getAttribute(Attribute.GENERIC_MAX_HEALTH);
			attr.setBaseValue(attr.getDefaultValue());
		}
	}

}
