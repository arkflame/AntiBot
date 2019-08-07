package twolovers.antibot.bungee.modules;

import net.md_5.bungee.config.Configuration;
import twolovers.antibot.bungee.managers.ModuleManager;
import twolovers.antibot.bungee.utils.ConfigurationUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AccountsModule {
	private final ConfigurationUtil configurationUtil;
	private final ModuleManager moduleManager;
	private boolean enabled = true;
	private Map<String, Set<String>> accounts = new HashMap<>();
	private String kickMessage = "";
	private int limit = 2;
	private int PPSCondition = 0;
	private int CPSCondition = 0;
	private int JPSCondition = 0;

	public AccountsModule(ConfigurationUtil configurationUtil, ModuleManager moduleManager) {
		this.configurationUtil = configurationUtil;
		this.moduleManager = moduleManager;
		reload();
	}

	public final void reload() {
		final Configuration config = this.configurationUtil.getConfiguration("%datafolder%/config.yml");

		if (config != null) {
			enabled = config.getBoolean("accounts.enabled");
			kickMessage = config.getString("accounts.kick_message").replace("&", "\u00A7");
			limit = config.getInt("accounts.limit");

			for (String string : config.getStringList("accounts.conditions")) {
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
		return enabled && ((moduleManager.getTotalPPS() >= PPSCondition && moduleManager.getTotalCPS() >= CPSCondition && moduleManager.getTotalJPS() >= JPSCondition) || (moduleManager.getLastPPS() >= PPSCondition && moduleManager.getLastCPS() >= CPSCondition && moduleManager.getLastJPS() >= JPSCondition));
	}

	public final String getKickMessage() {
		return kickMessage;
	}

	public final void addAccount(String ip, String name) {
		final Set<String> accounts = this.accounts.getOrDefault(ip, new HashSet<>());

		if (!accounts.contains(name)) {
			accounts.add(name);
			this.accounts.put(ip, accounts);
		}
	}

	public final int getAccountCount(String ip) {
		return accounts.getOrDefault(ip, new HashSet<>()).size();
	}

	public final int getLimit() {
		return limit;
	}
}
