package twolovers.antibot.bungee.module;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import twolovers.antibot.bungee.instanceables.BotPlayer;
import twolovers.antibot.bungee.utils.ConfigUtil;
import twolovers.antibot.shared.interfaces.Module;

public class PlaceholderModule implements Module {
	private final String name = "placeholder";
	private final Plugin plugin;
	private final ModuleManager moduleManager;
	private final Map<String, String> placeholders;
	private String lang;

	PlaceholderModule(final Plugin plugin, final ModuleManager moduleManager) {
		this.plugin = plugin;
		this.moduleManager = moduleManager;
		this.placeholders = new HashMap<>();
	}

	@Override
	public String getName() {
		return name;
	}

	public String replacePlaceholders(final String locale, String string, final String address, final String checkName,
			final AtomicInteger counter) {
		final ReconnectModule reconnectModule = moduleManager.getReconnectModule();
		final PlayerModule playerModule = moduleManager.getPlayerModule();
		final BotPlayer botPlayer = playerModule.get(address);
		final int count = counter.getAndIncrement();

		if (count >= 50) {
			if (count == 50) {
				plugin.getLogger().warning(
						"A StackOverflow ocurred while replacing placeholders because a message was translated incorrectly. This is probably due to messages.yml being corrupt or modified wrongly.");
			}

			return string;
		} else {
			for (final String key : placeholders.keySet()) {
				final String value = placeholders.get(key);

				if (string.contains(key)) {
					string = replacePlaceholders(locale, string.replace(key, value), address, checkName, counter);
					break;
				} else {
					final String keyLocaleReplaced = key.replace("%" + locale + "_", "%");

					if (string.contains(keyLocaleReplaced)) {
						string = replacePlaceholders(locale, string.replace(keyLocaleReplaced, value), address,
								checkName, counter);
						break;
					} else {
						final String keyLangReplaced = key.replace("%" + lang + "_", "%");

						if (string.contains(keyLangReplaced)) {
							string = replacePlaceholders(locale, string.replace(keyLangReplaced, value), address,
									checkName, counter);
							break;
						}
					}
				}
			}
		}

		final int reconnects = botPlayer.getReconnects(), timesConnect = reconnectModule.getTimesConnect(),
				reconnectTimes = reconnects > timesConnect ? 0 : timesConnect - reconnects;

		return ChatColor.translateAlternateColorCodes('&',
				string.replace("%check%", checkName).replace("%version%", plugin.getDescription().getVersion())
						.replace("%lastpps%", String.valueOf(moduleManager.getLastPPS()))
						.replace("%lastcps%", String.valueOf(moduleManager.getLastCPS()))
						.replace("%lastjps%", String.valueOf(moduleManager.getLastJPS()))
						.replace("%currentpps%", String.valueOf(moduleManager.getCurrentPPS()))
						.replace("%currentcps%", String.valueOf(moduleManager.getCurrentCPS()))
						.replace("%currentjps%", String.valueOf(moduleManager.getCurrentJPS()))
						.replace("%addresspps%", String.valueOf(botPlayer.getPPS()))
						.replace("%addresscps%", String.valueOf(botPlayer.getCPS()))
						.replace("%addressjps%", String.valueOf(botPlayer.getJPS()))
						.replace("%totalbls%", String.valueOf(moduleManager.getBlacklistModule().getSize()))
						.replace("%totalwls%", String.valueOf(moduleManager.getWhitelistModule().getSize()))
						.replace("%address%", address).replace("%reconnect_times%", String.valueOf(reconnectTimes)));
	}

	@Override
	public void reload(final ConfigUtil configUtil) {
		final Configuration configYml = configUtil.getConfiguration("%datafolder%/config.yml");
		final Configuration messagesYml = configUtil.getConfiguration("%datafolder%/messages.yml");
		final StringBuilder path = new StringBuilder();

		this.lang = configYml.getString("lang");
		this.placeholders.clear();
		addSection(messagesYml, path, messagesYml);
	}

	private void addSection(final Configuration messagesYml, final StringBuilder path, final Configuration section) {
		for (final String key : section.getKeys()) {
			final Object value = section.get(key);

			if (value instanceof Configuration)
				addSection(messagesYml, new StringBuilder(path).append(".").append(key), (Configuration) value);
			else if (value instanceof String)
				placeholders.put(("%" + path.toString() + "." + key + "%").replace(".", "_").replace("%_", "%"),
						(String) value);
		}
	}
}