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
	private final Plugin plugin;
	private final ModuleManager moduleManager;
	private final Map<String, String> placeholders;
	private String lang;

	PlaceholderModule(final Plugin plugin, final ModuleManager moduleManager) {
		this.plugin = plugin;
		this.moduleManager = moduleManager;
		this.placeholders = new HashMap<>();
	}

	public String replacePlaceholders(final String locale, String string, final String address,
			final String checkName) {
		final ReconnectModule reconnectModule = moduleManager.getReconnectModule();
		final PlayerModule playerModule = moduleManager.getPlayerModule();
		final BotPlayer botPlayer = playerModule.get(address);

		for (final String key : placeholders.keySet()) {
			string = string.replace(key, placeholders.get(key));
			string = string.replace(key.replace("%" + locale + "_", "%"), placeholders.get(key));
			string = string.replace(key.replace("%" + lang + "_", "%"), placeholders.get(key));
		}

		return ChatColor.translateAlternateColorCodes('&',
				string.replace("%check%", checkName).replace("%version%", plugin.getDescription().getVersion())
						.replace("%currentpps%", String.valueOf(moduleManager.getCurrentPPS()))
						.replace("%currentcps%", String.valueOf(moduleManager.getCurrentCPS()))
						.replace("%currentjps%", String.valueOf(moduleManager.getCurrentJPS()))
						.replace("%addresspps%", String.valueOf(botPlayer.getPPS()))
						.replace("%addresscps%", String.valueOf(botPlayer.getCPS()))
						.replace("%addressjps%", String.valueOf(botPlayer.getJPS()))
						.replace("%totalbls%", String.valueOf(moduleManager.getBlacklistModule().getSize()))
						.replace("%totalwls%", String.valueOf(moduleManager.getWhitelistModule().getSize()))
						.replace("%address%", address).replace("%reconnect_times%",
								String.valueOf(reconnectModule.getTimes() - botPlayer.getReconnects())));
	}

	@Override
	public String getName() {
		return "placeholder";
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