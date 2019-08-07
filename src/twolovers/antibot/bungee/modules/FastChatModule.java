package twolovers.antibot.bungee.modules;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.config.Configuration;
import twolovers.antibot.bungee.managers.ModuleManager;
import twolovers.antibot.bungee.utils.ConfigurationUtil;

public class FastChatModule {
	private final ConfigurationUtil configurationUtil;
	private final ModuleManager moduleManager;
	private boolean fastChatEnabled = true;
	private long fastChatTime = 750;
	private String fastChatKickMessage = "";
	private int PPSCondition = 0;
	private int CPSCondition = 0;
	private int JPSCondition = 0;

	public FastChatModule(ConfigurationUtil configurationUtil, ModuleManager moduleManager) {
		this.configurationUtil = configurationUtil;
		this.moduleManager = moduleManager;
		reload();
	}

	public final void reload() {
		final Configuration config = this.configurationUtil.getConfiguration("%datafolder%/config.yml");

		if (config != null) {
			fastChatEnabled = config.getBoolean("fastchat.enabled");
			fastChatTime = config.getInt("fastchat.time");
			fastChatKickMessage = ChatColor.translateAlternateColorCodes('&', config.getString("fastchat.kick_message"));

			for (String string : config.getStringList("fastchat.conditions")) {
				String[] strings = string.split("([|])");
				int value = Integer.parseInt(strings[0]);
				String type = strings[1];

				if (type.equals("PPS")) {
					PPSCondition = value;
				} else if (type.equals("CPS")) {
					CPSCondition = value;
				} else {
					JPSCondition = value;
				}
			}
		}
	}

	public final boolean isCondition(String ip) {
		return fastChatEnabled && ((moduleManager.getTotalPPS() >= PPSCondition && moduleManager.getTotalCPS() >= CPSCondition && moduleManager.getTotalJPS() >= JPSCondition) || (moduleManager.getLastPPS() >= PPSCondition && moduleManager.getLastCPS() >= CPSCondition && moduleManager.getLastJPS() >= JPSCondition)) && System.currentTimeMillis() - moduleManager.getLastConnection(ip) < fastChatTime;
	}

	public final String getKickMessage() {
		return fastChatKickMessage;
	}
}
