package twolovers.antibot.bungee.modules;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.api.ChatColor;
import twolovers.antibot.bungee.managers.ModuleManager;
import twolovers.antibot.bungee.utils.ConfigurationUtil;

public class RegisterModule {
	private final ConfigurationUtil configurationUtil;
	private final ModuleManager moduleManager;
	private boolean enabled = true;
	private String lastRegisterIp = "";
	private String lastRegisterCommand = "/register AAAAAAAAAAAAAAAAA";
	private String kickMessage = "";
	private int PPSCondition = 0;
	private int CPSCondition = 0;
	private int JPSCondition = 0;

	public RegisterModule(ConfigurationUtil configurationUtil, ModuleManager moduleManager) {
		this.configurationUtil = configurationUtil;
		this.moduleManager = moduleManager;
		reloadVariables();
	}

	public final void reloadVariables() {
		final Configuration config = this.configurationUtil.getConfiguration("%datafolder%/config.yml");

		if (config != null) {
			enabled = config.getBoolean("register.enabled");
			kickMessage = ChatColor.translateAlternateColorCodes('&', config.getString("register.kick_message"));

			for (String string : config.getStringList("register.conditions")) {
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

	public final boolean isCondition(String ip, String message) {
		return enabled && ((moduleManager.getTotalPPS() >= PPSCondition && moduleManager.getTotalCPS() >= CPSCondition && moduleManager.getTotalJPS() >= JPSCondition) || (moduleManager.getLastPPS() >= PPSCondition && moduleManager.getLastCPS() >= CPSCondition && moduleManager.getLastJPS() >= JPSCondition)) && !ip.equals(lastRegisterIp) && message.equals(lastRegisterCommand);
	}

	public final String getKickMessage() {
		return kickMessage;
	}

	public final void setLastRegisterCommand(String ip, String command) {
		lastRegisterIp = ip;
		lastRegisterCommand = command;
	}
}
