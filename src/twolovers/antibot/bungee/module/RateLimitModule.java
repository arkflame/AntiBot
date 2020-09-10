package twolovers.antibot.bungee.module;

import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.config.Configuration;
import twolovers.antibot.bungee.instanceables.BotPlayer;
import twolovers.antibot.bungee.instanceables.Conditions;
import twolovers.antibot.bungee.utils.ConfigUtil;
import twolovers.antibot.shared.extendables.PunishableModule;

public class RateLimitModule extends PunishableModule {
	private static final String NAME = "ratelimit";
	private final ModuleManager moduleManager;
	private int maxOnline = 3;
	private int throttle = 800;

	RateLimitModule(final ModuleManager moduleManager) {
		this.moduleManager = moduleManager;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public final void reload(final ConfigUtil configUtil) {
		final Configuration configYml = configUtil.getConfiguration("%datafolder%/config.yml");
		final int pps = configYml.getInt(NAME + ".limits.pps", 0);
		final int cps = configYml.getInt(NAME + ".limits.cps", 0);
		final int jps = configYml.getInt(NAME + ".limits.jps", 0);

		enabled = configYml.getBoolean(NAME + ".enabled", enabled);
		punishCommands.clear();
		punishCommands.addAll(configYml.getStringList(NAME + ".commands"));
		conditions = new Conditions(pps, cps, jps, true);
		maxOnline = configYml.getInt(NAME + ".max_online", maxOnline);
		throttle = configYml.getInt(NAME + ".throttle", throttle);
	}

	public boolean check(final Connection connection) {
		final PlayerModule playerModule = moduleManager.getPlayerModule();
		final BotPlayer botPlayer = playerModule.get(connection.getAddress().getHostString());
		final int pps = botPlayer.getPPS(), cps = botPlayer.getCPS(), jps = botPlayer.getJPS();
		final long lastConnection = botPlayer.getLastConnection();
		final boolean isThrottle = (cps == 0 && pps >= 0) ? false
				: System.currentTimeMillis() - lastConnection < throttle;

		return conditions.meet(pps, cps, jps, pps, cps, jps) || isThrottle
				|| botPlayer.getAccounts().size() > maxOnline;
	}
}
