package twolovers.antibot.bungee.module;

import java.util.HashMap;
import java.util.Map;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import twolovers.antibot.bungee.instanceables.BotPlayer;
import twolovers.antibot.bungee.utils.ConfigUtil;
import twolovers.antibot.shared.interfaces.Module;

public class PlaceholderModule implements Module {
	private final String name = "placeholder";
	private final String pluginVersion;
	private final Map<String, String> placeholders = new HashMap<>();
	private String lang;

	PlaceholderModule(final Plugin plugin) {
		this.pluginVersion = plugin.getDescription().getVersion();
	}

	@Override
	public String getName() {
		return this.name;
	}

	private final String setPlaceholders(final String... args) {
		return setPlaceholders(null, args);
	}

	public final String setPlaceholders(final ModuleManager moduleManager, final String... args) {
		final int length = args.length;

		if (length > 0) {
			final String string = args[0];

			if (length > 1) {
				final String locale = args[1];

				if (length > 2) {
					final String address = args[2];

					if (length > 3) {
						final String checkName = args[3];

						return setPlaceholders(string, locale, address, checkName, moduleManager);
					} else {
						return setPlaceholders(string, locale, address, null, moduleManager);
					}
				} else {
					return setPlaceholders(string, locale, null, null, moduleManager);
				}
			} else {
				return setPlaceholders(string, null, null, null, moduleManager);
			}
		} else {
			return "N/A";
		}
	}

	private final String setPlaceholders(String string, final String locale, final String address,
			final String checkName, final ModuleManager moduleManager) {
		for (final String key : placeholders.keySet()) {
			final String value = placeholders.get(key);

			if (string.contains(key)) {
				string = string.replace(key, value);
			} else if (locale != null) {
				final String keyLocaleReplaced = key.replace("%" + locale + "_", "%");

				if (string.contains(keyLocaleReplaced)) {
					string = string.replace(keyLocaleReplaced, value);
				} else {
					final String keyLangReplaced = key.replace("%" + lang + "_", "%");

					if (string.contains(keyLangReplaced)) {
						string = string.replace(keyLangReplaced, value);
					}
				}
			}
		}

		if (moduleManager != null) {
			if (address != null) {
				final ReconnectModule reconnectModule = moduleManager.getReconnectModule();
				final PlayerModule playerModule = moduleManager.getPlayerModule();
				final BotPlayer botPlayer = playerModule.get(address);

				if (botPlayer != null) {
					final int reconnects = botPlayer.getReconnects(), timesConnect = reconnectModule.getTimesConnect(),
							reconnectTimes = reconnects > timesConnect ? 0 : timesConnect - reconnects;

					string = string.replace("%reconnect_times%", String.valueOf(reconnectTimes))
							.replace("%addresspps%", String.valueOf(botPlayer.getPPS()))
							.replace("%addresscps%", String.valueOf(botPlayer.getCPS()))
							.replace("%addressjps%", String.valueOf(botPlayer.getJPS())).replace("%address%", address);
				}
			}

			string = string.replace("%lastpps%", String.valueOf(moduleManager.getLastPPS()))
					.replace("%lastcps%", String.valueOf(moduleManager.getLastCPS()))
					.replace("%lastjps%", String.valueOf(moduleManager.getLastJPS()))
					.replace("%currentpps%", String.valueOf(moduleManager.getCurrentPPS()))
					.replace("%currentcps%", String.valueOf(moduleManager.getCurrentCPS()))
					.replace("%currentjps%", String.valueOf(moduleManager.getCurrentJPS()))
					.replace("%totalbls%", String.valueOf(moduleManager.getBlacklistModule().getSize()))
					.replace("%totalwls%", String.valueOf(moduleManager.getWhitelistModule().getSize()));
		}

		if (checkName != null) {
			string.replace("%check%", checkName);
		}

		return ChatColor.translateAlternateColorCodes('&', string.replace("%version%", pluginVersion));
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

			if (value instanceof Configuration) {
				addSection(messagesYml, new StringBuilder(path).append(".").append(key), (Configuration) value);
			} else if (value instanceof String) {
				placeholders.put(("%" + new StringBuilder(path).toString() + "." + key + "%").replace(".", "_")
						.replace("%_", "%"), setPlaceholders((String) value));
			}
		}
	}
}