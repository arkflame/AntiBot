package twolovers.antibot.bungee.module;

import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.config.Configuration;
import twolovers.antibot.bungee.instanceables.BotPlayer;
import twolovers.antibot.bungee.utils.ConfigUtil;
import twolovers.antibot.shared.extendables.PunishableModule;

public class FastChatModule extends PunishableModule {
	private final ModuleManager moduleManager;
	private int time = 1000;

	FastChatModule(final ModuleManager moduleManager) {
		this.moduleManager = moduleManager;
	}

	@Override
	public final void reload(final ConfigUtil configUtil) {
		super.name = "fastchat";
		super.reload(configUtil);

		final Configuration configYml = configUtil.getConfiguration("%datafolder%/config.yml");

		punishCommands.clear();
		punishCommands.addAll(configYml.getStringList(name + ".commands"));
		time = configYml.getInt(name + ".time", time);
	}

	public boolean check(final Connection connection) {
		final PlayerModule playerModule = moduleManager.getPlayerModule();
		final BotPlayer botPlayer = playerModule.get(connection.getAddress().getHostString());

		return (botPlayer == null || System.currentTimeMillis() - botPlayer.getLastConnection() < time
				|| !botPlayer.isSettings());
	}
}
