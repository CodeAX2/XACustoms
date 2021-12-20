package dev.jd.xacustoms;

import java.io.File;
import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class CurrencyHandler {

	private File currencyFile;
	private FileConfiguration currencyConfig;

	private App plugin;

	private HashMap<UUID, Long> timeOnlines;
	private HashMap<UUID, Date> lastResets;
	private HashMap<UUID, Integer> rewardAmounts;

	private int loopTaskId;

	public CurrencyHandler(String fileName, App plugin) {
		this.plugin = plugin;

		createConfig(fileName);

		timeOnlines = new HashMap<>();
		lastResets = new HashMap<>();
		rewardAmounts = new HashMap<>();

		loadConfig();

	}

	private void createConfig(String fileName) {
		currencyFile = new File(plugin.getDataFolder(), fileName);

		if (!currencyFile.exists()) {
			currencyFile.getParentFile().mkdirs();
			try {
				currencyFile.createNewFile();
			} catch (IOException e) {
				plugin.getLogger().log(Level.WARNING, "Unable to create file for currency handler: " + fileName, e);
			}
		}

		currencyConfig = new YamlConfiguration();
		try {
			currencyConfig.load(currencyFile);
		} catch (IOException | InvalidConfigurationException e) {
			plugin.getLogger().log(Level.WARNING, "Unable to load config for currency handler: " + fileName, e);
		}
	}

	private void loadConfig() {

		for (String s : currencyConfig.getKeys(false)) {
			UUID curPlayerUUID = UUID.fromString(s);

			Long curTimeOnline = currencyConfig.getLong(s + ".timeOnline");
			timeOnlines.put(curPlayerUUID, curTimeOnline);

			Date curLastReset = currencyConfig.getObject(s + ".lastReset", Date.class);
			lastResets.put(curPlayerUUID, curLastReset);

			Integer curRewardAmount = currencyConfig.getInt(s + ".rewardAmount");
			rewardAmounts.put(curPlayerUUID, curRewardAmount);
		}

	}

	private void saveConfig() {

		for (UUID curPlauerId : timeOnlines.keySet()) {
			currencyConfig.set(curPlauerId.toString() + ".timeOnline", timeOnlines.get(curPlauerId));
			currencyConfig.set(curPlauerId.toString() + ".lastReset", lastResets.get(curPlauerId));
			currencyConfig.set(curPlauerId.toString() + ".rewardAmount", rewardAmounts.get(curPlauerId));
		}

		try {
			currencyConfig.save(currencyFile);
		} catch (IOException e) {
			plugin.getLogger().log(Level.WARNING,
					"Unable to save config for currency handler: " + currencyFile.getName(), e);
		}

	}

	public void startLoop() {
		loopTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new CurrencyLoop(), 0L, 20L);
	}

	public void shutdown() {
		Bukkit.getScheduler().cancelTask(loopTaskId);
		saveConfig();
	}

	private class CurrencyLoop implements Runnable {

		private int secondsSinceLastNotify = 0;

		@Override
		public void run() {

			boolean shouldNotify = secondsSinceLastNotify >= plugin.getConfig().getInt("currency.notifyTime");

			Player[] online = new Player[Bukkit.getOnlinePlayers().size()];
			online = Bukkit.getOnlinePlayers().toArray(online);
			Date now = new Date();

			for (Player p : online) {
				UUID playerUUID = p.getUniqueId();
				// Check if they have a time
				if (!timeOnlines.containsKey(playerUUID)) {
					timeOnlines.put(playerUUID, 0L);
					lastResets.put(playerUUID, now);
					// Give them first time rewards here
					rewardAmounts.put(playerUUID, plugin.getConfig().getInt("currency.firstJoinAmount"));
				} else {
					// Check when the last reset was
					Date lastReset = lastResets.get(playerUUID);
					long daysSinceLastReset = ChronoUnit.DAYS.between(lastReset.toInstant(), now.toInstant());
					if (daysSinceLastReset >= 7) {
						// One week has passed, check for rewards
						if (timeOnlines.get(playerUUID) >= plugin.getConfig().getLong("currency.weeklyTime")) {
							// Player met the requirement for reward
							Integer newReward = rewardAmounts.get(playerUUID)
									+ plugin.getConfig().getInt("currency.weeklyAmount");
							rewardAmounts.put(playerUUID, newReward);
						}

						timeOnlines.put(playerUUID, 0L);
						lastResets.put(playerUUID, now);
					} else {
						// Less than a week has passed, so just increment their time
						Long timeOnline = timeOnlines.get(playerUUID);
						timeOnlines.put(playerUUID, timeOnline + 1);
					}
				}

				if (rewardAmounts.get(playerUUID) != 0 && shouldNotify) {

					String message = ChatColor.translateAlternateColorCodes('&',
							plugin.getConfig().getString("currency.rewardMessage")) + " ";
					// Added space to end to ensure there is always a non-empty chunk at the end

					message = message.replace("%name%", p.getDisplayName());

					message = message.replace("%rewardAmount%", rewardAmounts.get(playerUUID) + "");

					String[] messageChunks = message.split("%rewardClick%");

					TextComponent rewardClickComponent = new TextComponent(
							ChatColor.translateAlternateColorCodes('&',
									plugin.getConfig().getString("currency.rewardClickMessage")));
					rewardClickComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/getcurrency"));
					rewardClickComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
							new Text(ChatColor.translateAlternateColorCodes('&',
									plugin.getConfig().getString("currency.rewardHoverMessage")))));

					ComponentBuilder cb = new ComponentBuilder();

					for (int i = 0; i < messageChunks.length; i++) {
						String chunk = messageChunks[i];
						if (i == messageChunks.length - 1) {
							// Remove tailing space from last chunk
							chunk = chunk.substring(0, chunk.length() - 1);
						}
						cb.append(chunk);

						if (i != messageChunks.length - 1)
							cb.append(rewardClickComponent);
					}

					p.spigot().sendMessage(cb.create());
					p.playSound(p.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_STEP, SoundCategory.BLOCKS, 1, 1);
				}

			}

			secondsSinceLastNotify++;
			if (shouldNotify)
				secondsSinceLastNotify = 0;

		}

	}

	public void givePlayerCurrency(Player p) {
		if (rewardAmounts.containsKey(p.getUniqueId()) && rewardAmounts.get(p.getUniqueId()) != 0) {
			ItemStack currencyStack = CustomItems.getCurrency();
			currencyStack.setAmount(rewardAmounts.get(p.getUniqueId()));
			HashMap<Integer, ItemStack> ungiven = p.getInventory().addItem(currencyStack);
			for (Integer i : ungiven.keySet()) {
				p.getWorld().dropItem(p.getLocation(), ungiven.get(i));
			}
			rewardAmounts.put(p.getUniqueId(), 0);
		}
	}

}
