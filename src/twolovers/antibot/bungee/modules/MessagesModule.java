package twolovers.antibot.bungee.modules;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.api.ChatColor;
import twolovers.antibot.bungee.managers.ModuleManager;
import twolovers.antibot.bungee.utils.ConfigurationUtil;

public class MessagesModule {
	private final ConfigurationUtil configurationUtil;
	private final ModuleManager moduleManager;
	private String reload;
	private String help;
	private String unknownCommand;
	private String noPermission;
	private String stats;

	public MessagesModule(ConfigurationUtil configurationUtil, ModuleManager moduleManager) {
		this.configurationUtil = configurationUtil;
		this.moduleManager = moduleManager;
		reload();
	}

	public final void reload() {
		final Configuration messages = configurationUtil.getConfiguration("%datafolder%/messages.yml");

		if (messages != null) {
			reload = ChatColor.translateAlternateColorCodes('&', messages.getString("reload").replace("&", "§"));
			help = ChatColor.translateAlternateColorCodes('&', messages.getString("help").replace("&", "§"));
			unknownCommand = ChatColor.translateAlternateColorCodes('&', messages.getString("unknowncommand"));
			noPermission = ChatColor.translateAlternateColorCodes('&', messages.getString("nopermission"));
			stats = ChatColor.translateAlternateColorCodes('&', messages.getString("stats"));
		}
	}

	public final String getReload() {
		return reload;
	}

	public final String getHelp() {
		return help;
	}

	public final String getUnknownCommand() {
		return unknownCommand;
	}

	public final String getNoPermission() {
		return noPermission;
	}

	public final String getStats() {
		BlacklistModule blacklistModule = moduleManager.getBlacklistModule();
		WhitelistModule whitelistModule = moduleManager.getWhitelistModule();

		return stats
				.replace("%totalpps%", String.valueOf(moduleManager.getLastPPS()))
				.replace("%totalcps%", String.valueOf(moduleManager.getLastCPS()))
				.replace("%totaljps%", String.valueOf(moduleManager.getLastJPS()))
				.replace("%totalbls%", String.valueOf(blacklistModule.getBlacklistSize()))
				.replace("%totalwls%", String.valueOf(whitelistModule.getWhitelistSize()));
	}
}