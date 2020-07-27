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
	private final ModuleManager moduleManager;
	private final Map<String, String> placeholders = new HashMap<>();
	private String lang;

	PlaceholderModule(final Plugin plugin, final ModuleManager moduleManager) {
		this.pluginVersion = plugin.getDescription().getVersion();
		this.moduleManager = moduleManager;
	}

	@Override
	public String getName() {
		return name;
	}

	public final String setPlaceholders(final String string) {
		return setPlaceholders(null, string);
	}

	public final String setPlaceholders(final String locale, String string) {
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

		string = string.replace("%lastpps%", String.valueOf(moduleManager.getLastPPS()))
				.replace("%lastcps%", String.valueOf(moduleManager.getLastCPS()))
				.replace("%lastjps%", String.valueOf(moduleManager.getLastJPS()))
				.replace("%currentpps%", String.valueOf(moduleManager.getCurrentPPS()))
				.replace("%currentcps%", String.valueOf(moduleManager.getCurrentCPS()))
				.replace("%currentjps%", String.valueOf(moduleManager.getCurrentJPS()))
				.replace("%totalbls%", String.valueOf(moduleManager.getBlacklistModule().getSize()))
				.replace("%totalwls%", String.valueOf(moduleManager.getWhitelistModule().getSize()));

		return ChatColor.translateAlternateColorCodes('&', string.replace("%version%", pluginVersion));
	}

	public final String setPlaceholders(final String locale, String string, final String address) {
		return setPlaceholders(locale, string, string, null);
	}

	public final String setPlaceholders(final String locale, String string, final String address,
			final String checkName) {
		final ReconnectModule reconnectModule = moduleManager.getReconnectModule();
		final PlayerModule playerModule = moduleManager.getPlayerModule();

		if (address != null) {
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

		if (checkName != null) {
			string.replace("%check%", checkName);
		}

		return setPlaceholders(locale, string);
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