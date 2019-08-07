package twolovers.antibot.bungee.modules;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.api.ChatColor;
import twolovers.antibot.bungee.managers.ModuleManager;
import twolovers.antibot.bungee.utils.ConfigurationUtil;

import java.util.HashSet;
import java.util.Set;

public class SettingsModule {
	private final ConfigurationUtil configurationUtil;
	private final ModuleManager moduleManager;
	private final Set<String> switched = new HashSet<>();
	private boolean settingsEnabled = true;
	private String settingsKickMessage = "";
	private int PPSCondition = 0;
	private int CPSCondition = 0;
	private int JPSCondition = 0;

	public SettingsModule(ConfigurationUtil configurationUtil, ModuleManager moduleManager) {
		this.configurationUtil = configurationUtil;
		this.moduleManager = moduleManager;
		reload();
	}

	public final void reload() {
		final Configuration config = this.configurationUtil.getConfiguration("%datafolder%/config.yml");

		if (config != null) {
			settingsEnabled = config.getBoolean("settings.enabled");
			settingsKickMessage = ChatColor.translateAlternateColorCodes('&', config.getString("settings.kick_message"));

			for (String string : config.getStringList("settings.conditions")) {
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

	public final boolean isCondition() {
		return settingsEnabled && ((moduleManager.getTotalPPS() >= PPSCondition && moduleManager.getTotalCPS() >= CPSCondition && moduleManager.getTotalJPS() >= JPSCondition) || (moduleManager.getLastPPS() >= PPSCondition && moduleManager.getLastCPS() >= CPSCondition && moduleManager.getLastJPS() >= JPSCondition));
	}

	public final String getSettingsKickMessage() {
		return settingsKickMessage;
	}

	public final void setSwitched(String ip, Boolean input) {
		if (input) {
			switched.add(ip);
		} else {
			switched.remove(ip);
		}
	}

	public final boolean isSwitched(String ip) {
		return switched.contains(ip);
	}
}
