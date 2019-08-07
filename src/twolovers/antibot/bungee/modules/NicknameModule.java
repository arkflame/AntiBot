package twolovers.antibot.bungee.modules;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.config.Configuration;
import twolovers.antibot.bungee.managers.ModuleManager;
import twolovers.antibot.bungee.utils.ConfigurationUtil;

import java.util.ArrayList;
import java.util.List;

public class NicknameModule {
	private final ConfigurationUtil configurationUtil;
	private final ModuleManager moduleManager;
	private List<String> containsStrings = new ArrayList<>();
	private boolean nicknameEnabled = true;
	private String nicknameKickMessage = "";
	private int PPSCondition = 0;
	private int CPSCondition = 0;
	private int JPSCondition = 0;

	public NicknameModule(ConfigurationUtil configurationUtil, ModuleManager moduleManager) {
		this.configurationUtil = configurationUtil;
		this.moduleManager = moduleManager;
		reload();
	}

	public final void reload() {
		final Configuration config = this.configurationUtil.getConfiguration("%datafolder%/config.yml");

		if (config != null) {
			nicknameEnabled = config.getBoolean("nickname.enabled");
			nicknameKickMessage = ChatColor.translateAlternateColorCodes('&', config.getString("nickname.kick_message"));
			containsStrings = config.getStringList("nickname.contains");

			for (String string : config.getStringList("nickname.conditions")) {
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


	public boolean cotainsString(final String name) {
		if (containsStrings != null)
			for (final String brand : containsStrings)
				if (name.contains(brand))
					return true;

		return false;
	}

	public final boolean isCondition() {
		return nicknameEnabled && ((moduleManager.getTotalPPS() >= PPSCondition && moduleManager.getTotalCPS() >= CPSCondition && moduleManager.getTotalJPS() >= JPSCondition) || (moduleManager.getLastPPS() >= PPSCondition && moduleManager.getLastCPS() >= CPSCondition && moduleManager.getLastJPS() >= JPSCondition));
	}

	public final String getKickMessage() {
		return nicknameKickMessage;
	}

	public String getPattern() {
		return "^(Craft|Beach|Actor|Games|Tower|Elder|Mine|Nitro|Worms|Build|Plays|Hyper|Crazy|Super|_Itz|Slime)(Craft|Beach|Actor|Games|Tower|Elder|Mine|Nitro|Worms|Build|Plays|Hyper|Crazy|Super|_Itz|Slime)(11|50|69|99|88|HD|LP|XD|YT)";
	}
}
