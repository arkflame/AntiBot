package twolovers.antibot.bungee.module;

import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.config.Configuration;
import twolovers.antibot.bungee.instanceables.BotPlayer;
import twolovers.antibot.bungee.instanceables.Conditions;
import twolovers.antibot.bungee.utils.ConfigUtil;
import twolovers.antibot.shared.interfaces.PunishModule;

import java.util.Collection;
import java.util.HashSet;

public class FastChatModule implements PunishModule {
	private final String name = "fastchat";
	private final ModuleManager moduleManager;
	private Collection<String> punishCommands = new HashSet<>();
	private Conditions conditions;
	private boolean enabled = true;
	private long time = 750;

	FastChatModule(final ModuleManager moduleManager) {
		this.moduleManager = moduleManager;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public final void reload(final ConfigUtil configUtil) {
		final Configuration configYml = configUtil.getConfiguration("%datafolder%/config.yml");
		final int pps = configYml.getInt(name + ".conditions.pps", 0);
		final int cps = configYml.getInt(name + ".conditions.cps", 0);
		final int jps = configYml.getInt(name + ".conditions.jps", 0);

		enabled = configYml.getBoolean(name + ".enabled");
		punishCommands.clear();
		punishCommands.addAll(configYml.getStringList(name + ".commands"));
		conditions = new Conditions(pps, cps, jps, false);
		time = configYml.getInt(name + ".time");
	}

	@Override
	public boolean meet(int pps, int cps, int jps) {
		return this.enabled && conditions.meet(pps, cps, jps, moduleManager.getLastPPS(), moduleManager.getLastCPS(),
				moduleManager.getLastJPS());
	}

	@Override
	public boolean check(final Connection connection) {
		final PlayerModule playerModule = moduleManager.getPlayerModule();
		final BotPlayer botPlayer = playerModule.get(connection.getAddress().getHostString());

		return (botPlayer == null || System.currentTimeMillis() - botPlayer.getLastConnection() < time
				|| !botPlayer.isSettings());
	}

	@Override
	public Collection<String> getPunishCommands() {
		return punishCommands;
	}
}
