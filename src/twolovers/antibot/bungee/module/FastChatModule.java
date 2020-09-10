package twolovers.antibot.bungee.module;

import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.config.Configuration;
import twolovers.antibot.bungee.instanceables.BotPlayer;
import twolovers.antibot.bungee.instanceables.Conditions;
import twolovers.antibot.bungee.utils.ConfigUtil;
import twolovers.antibot.shared.extendables.PunishableModule;

public class FastChatModule extends PunishableModule {
	private static final String NAME = "fastchat";
	private final ModuleManager moduleManager;
	private int time = 1000;

	FastChatModule(final ModuleManager moduleManager) {
		this.moduleManager = moduleManager;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public final void reload(final ConfigUtil configUtil) {
		final Configuration configYml = configUtil.getConfiguration("%datafolder%/config.yml");
		final int pps = configYml.getInt(NAME + ".conditions.pps", 0);
		final int cps = configYml.getInt(NAME + ".conditions.cps", 0);
		final int jps = configYml.getInt(NAME + ".conditions.jps", 0);

		enabled = configYml.getBoolean(NAME + ".enabled", enabled);
		punishCommands.clear();
		punishCommands.addAll(configYml.getStringList(NAME + ".commands"));
		conditions = new Conditions(pps, cps, jps, false);
		time = configYml.getInt(NAME + ".time", time);
	}

	public boolean check(final Connection connection) {
		final PlayerModule playerModule = moduleManager.getPlayerModule();
		final BotPlayer botPlayer = playerModule.get(connection.getAddress().getHostString());

		return (botPlayer == null || System.currentTimeMillis() - botPlayer.getLastConnection() < time
				|| !botPlayer.isSettings());
	}
}
