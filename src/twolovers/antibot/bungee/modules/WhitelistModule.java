package twolovers.antibot.bungee.modules;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.api.ChatColor;
import twolovers.antibot.bungee.managers.ModuleManager;
import twolovers.antibot.bungee.utils.ConfigurationUtil;

import java.util.HashSet;
import java.util.Set;

public class WhitelistModule {
	private final ConfigurationUtil configurationUtil;
	private final ModuleManager moduleManager;
	private Set<String> whitelist = new HashSet<>();
	private boolean whitelistEnabled = true;
	private String whitelistKickMessage = "";
	private int PPSCondition = 0;
	private int CPSCondition = 0;
	private int JPSCondition = 0;

	public WhitelistModule(ConfigurationUtil configurationUtil, ModuleManager moduleManager) {
		this.configurationUtil = configurationUtil;
		this.moduleManager = moduleManager;
		loadWhitelist();
	}

	public final void reload() {
		final Configuration config = this.configurationUtil.getConfiguration("%datafolder%/config.yml");

		if (config != null) {
			whitelistEnabled = config.getBoolean("whitelist.enabled");
			whitelistKickMessage = ChatColor.translateAlternateColorCodes('&', config.getString("whitelist.kick_message"));

			for (String string : config.getStringList("whitelist.conditions")) {
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

	public boolean isEnabled() {
		return whitelistEnabled;
	}

	public boolean isCondition() {
		return whitelistEnabled && ((moduleManager.getTotalPPS() >= PPSCondition && moduleManager.getTotalCPS() >= CPSCondition && moduleManager.getTotalJPS() >= JPSCondition) || (moduleManager.getLastPPS() >= PPSCondition && moduleManager.getLastCPS() >= CPSCondition && moduleManager.getLastJPS() >= JPSCondition));
	}

	public boolean isWhitelisted(final String ip) {
		return whitelist.contains(ip);
	}

	public void setWhitelisted(final String ip, final boolean input) {
		if (input) {
			moduleManager.getBlacklistModule().setBlacklisted(ip, false);
			whitelist.add(ip);
		} else
			whitelist.remove(ip);
	}

	public String getKickMessage() {
		return whitelistKickMessage;
	}

	int getWhitelistSize() {
		return whitelist.size();
	}

	public final void saveWhitelist() {
		Configuration whitelistYml = configurationUtil.getConfiguration("%datafolder%/whitelist.yml");

		if (whitelistYml != null) {
			whitelistYml.set("whitelist", whitelist.toArray());
			configurationUtil.saveConfiguration(whitelistYml, "%datafolder%/whitelist.yml");
		}
	}

	public final void loadWhitelist() {
		Configuration whitelistYml = configurationUtil.getConfiguration("%datafolder%/whitelist.yml");

		if (whitelistYml != null)
			this.whitelist = new HashSet<>(whitelistYml.getStringList("whitelist"));
	}
}
