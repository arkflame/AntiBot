package twolovers.antibot.bungee.modules;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.config.Configuration;
import twolovers.antibot.bungee.managers.ModuleManager;
import twolovers.antibot.bungee.utils.ConfigurationUtil;

import java.util.HashMap;

public class ReconnectModule {
	private final ConfigurationUtil configurationUtil;
	private final ModuleManager moduleManager;
	private HashMap<String, Integer> reconnected = new HashMap<>();
	private boolean forceRejoinEnabled = true;
	private boolean forceRejoingPingEnabled = true;
	private int forceRejoinTimes = 1;
	private long forceRejoinTime = 3000;
	private String forceRejoinKickMessage = "";
	private int PPSCondition = 0;
	private int CPSCondition = 0;
	private int JPSCondition = 0;

	public ReconnectModule(ConfigurationUtil configurationUtil, ModuleManager moduleManager) {
		this.configurationUtil = configurationUtil;
		this.moduleManager = moduleManager;
		reload();
	}

	public final void reload() {
		final Configuration config = this.configurationUtil.getConfiguration("%datafolder%/config.yml");

		if (config != null) {
			forceRejoinEnabled = config.getBoolean("reconnect.enabled");
			forceRejoingPingEnabled = config.getBoolean("reconnect.ping");
			if (config.getInt("reconnect.times") != 0)
				forceRejoinTimes = config.getInt("reconnect.times");
			forceRejoinTime = config.getLong("reconnect.time");
			forceRejoinKickMessage = ChatColor.translateAlternateColorCodes('&', config.getString("reconnect.kick_message"));

			for (String string : config.getStringList("reconnect.conditions")) {
				String[] strings = string.split("([|])");
				int value = Integer.parseInt(strings[0]);
				String type = strings[1];

				if (type.equals("PPS"))
					PPSCondition = value;
				else if (type.equals("CPS"))
					CPSCondition = value;
				else
					JPSCondition = value;
			}
		}
	}

	public boolean isCondition() {
		return forceRejoinEnabled && ((moduleManager.getTotalPPS() >= PPSCondition && moduleManager.getTotalCPS() >= CPSCondition && moduleManager.getTotalJPS() >= JPSCondition) || (moduleManager.getLastPPS() >= PPSCondition && moduleManager.getLastCPS() >= CPSCondition && moduleManager.getLastJPS() >= JPSCondition));
	}

	public boolean isForceRejoinPingEnabled() {
		return forceRejoingPingEnabled;
	}

	public long getReconnectTime() {
		return forceRejoinTime;
	}

	public String getKickMessage(final String ip) {
		return forceRejoinKickMessage
				.replace("%time%", String.valueOf(getReconnectTime() / 1000))
				.replace("%times%", String.valueOf(getReconnectTimes() - getReconnects(ip) + 1));
	}

	public void addReconnect(final String ip, int input) {
		if (input != 0)
			reconnected.put(ip, reconnected.getOrDefault(ip, 0) + input);
		else
			reconnected.remove(ip);
	}

	public int getReconnects(final String ip) {
		return reconnected.getOrDefault(ip, 0);
	}

	public int getReconnectTimes() {
		return forceRejoinTimes;
	}
}
