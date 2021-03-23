package twolovers.antibot.bungee.module;

import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.config.Configuration;
import twolovers.antibot.bungee.instanceables.BotPlayer;
import twolovers.antibot.bungee.utils.ConfigUtil;
import twolovers.antibot.bungee.utils.Incoming;
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
		final Incoming incoming = botPlayer.getIncoming();
		final long lastConnection = botPlayer.getLastConnection();
		final boolean isThrottle = (incoming.getCPS() != 0 || incoming.getPPS() < 0) && System.currentTimeMillis() - lastConnection < throttle;

		return thresholds.meet(incoming) || isThrottle
				|| botPlayer.getAccounts().size() > maxOnline;
	}
}
