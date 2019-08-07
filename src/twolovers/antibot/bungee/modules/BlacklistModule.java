package twolovers.antibot.bungee.modules;

import net.md_5.bungee.config.Configuration;
import twolovers.antibot.bungee.managers.ModuleManager;
import twolovers.antibot.bungee.utils.ConfigurationUtil;

import java.util.HashSet;
import java.util.Set;

public class BlacklistModule {
	private final ConfigurationUtil configurationUtil;
	private final ModuleManager moduleManager;
	private Set<String> blacklist = new HashSet<>();
	private boolean blacklistEnabled = true;
	private String blacklistKickMessage = "";
	private int PPSCondition = 0;
	private int CPSCondition = 0;
	private int JPSCondition = 0;

	public BlacklistModule(final ConfigurationUtil configurationUtil, final ModuleManager moduleManager) {
		this.configurationUtil = configurationUtil;
		this.moduleManager = moduleManager;
		reload();
		loadBlacklist();
	}

	public final void reload() {
		final Configuration config = this.configurationUtil.getConfiguration("%datafolder%/config.yml");

		if (config != null) {
			blacklistEnabled = config.getBoolean("blacklist.enabled");
			blacklistKickMessage = config.getString("blacklist.kick_message").replace("&", "\u00A7");

			for (String string : config.getStringList("blacklist.conditions")) {
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

	public boolean isCondition(String ip) {
		return blacklistEnabled && isBlacklisted(ip) && ((moduleManager.getTotalPPS() >= PPSCondition && moduleManager.getTotalCPS() >= CPSCondition && moduleManager.getTotalJPS() >= JPSCondition) || (moduleManager.getLastPPS() >= PPSCondition && moduleManager.getLastCPS() >= CPSCondition && moduleManager.getLastJPS() >= JPSCondition));
	}

	public boolean isBlacklisted(final String ip) {
		return blacklist.contains(ip);
	}

	public String getKickMessage() {
		return blacklistKickMessage;
	}

	public void setBlacklisted(final String ip, final boolean input) {
		moduleManager.getReconnectModule().addReconnect(ip, 0);

		if (input) {
			moduleManager.getWhitelistModule().setWhitelisted(ip, false);
			blacklist.add(ip);
		} else
			blacklist.remove(ip);
	}

	final int getBlacklistSize() {
		return blacklist.size();
	}

	public void saveBlacklist() {
		Configuration blacklistYml = configurationUtil.getConfiguration("%datafolder%/blacklist.yml");

		if (blacklistYml != null) {
			blacklistYml.set("blacklist", blacklist.toArray());
			configurationUtil.saveConfiguration(blacklistYml, "%datafolder%/blacklist.yml");
		}
	}

	public void loadBlacklist() {
		final Configuration blacklistYml = configurationUtil.getConfiguration("%datafolder%/blacklist.yml");

		if (blacklistYml != null)
			this.blacklist = new HashSet<>(blacklistYml.getStringList("blacklist"));
	}
}
