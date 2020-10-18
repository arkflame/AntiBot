package twolovers.antibot.bungee.module;

import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.config.Configuration;
import twolovers.antibot.bungee.instanceables.BotPlayer;
import twolovers.antibot.bungee.utils.ConfigUtil;
import twolovers.antibot.shared.extendables.PunishableModule;

public class RateLimitModule extends PunishableModule {
	private final ModuleManager moduleManager;
	private int maxOnline = 3;
	private int throttle = 800;

	RateLimitModule(final ModuleManager moduleManager) {
		this.moduleManager = moduleManager;
	}

	@Override
	public final void reload(final ConfigUtil configUtil) {
		super.name = "ratelimit";
		super.reload(configUtil);

		final Configuration configYml = configUtil.getConfiguration("%datafolder%/config.yml");

		punishCommands.clear();
		punishCommands.addAll(configYml.getStringList(name + ".commands"));
		maxOnline = configYml.getInt(name + ".max_online", maxOnline);
		throttle = configYml.getInt(name + ".throttle", throttle);
	}

	public boolean check(final Connection connection) {
		final PlayerModule playerModule = moduleManager.getPlayerModule();
		final BotPlayer botPlayer = playerModule.get(connection.getAddress().getHostString());
		final int pps = botPlayer.getPPS(), cps = botPlayer.getCPS(), jps = botPlayer.getJPS();
		final long lastConnection = botPlayer.getLastConnection();
		final boolean isThrottle = (cps == 0 && pps >= 0) ? false
				: System.currentTimeMillis() - lastConnection < throttle;

		return thresholds.meet(pps, cps, jps, pps, cps, jps) || isThrottle
				|| botPlayer.getAccounts().size() > maxOnline;
	}
}
