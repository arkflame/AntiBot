package twolovers.antibot.bungee.module;

import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.config.Configuration;
import twolovers.antibot.bungee.instanceables.BotPlayer;
import twolovers.antibot.bungee.instanceables.Conditions;
import twolovers.antibot.bungee.utils.ConfigUtil;
import twolovers.antibot.shared.extendables.PunishableModule;

public class ReconnectModule extends PunishableModule {
	private static final String NAME = "reconnect";
	private final ModuleManager moduleManager;
	private int timesPing = 1;
	private int timesConnect = 3;
	private long throttle = 800;

	ReconnectModule(final ModuleManager moduleManager) {
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
		timesPing = configYml.getInt(NAME + ".times.ping", timesPing);
		timesConnect = configYml.getInt(NAME + ".times.connect", timesConnect);
		throttle = configYml.getLong(NAME + ".throttle", throttle);
	}

	public boolean check(final Connection connection) {
		if (connection instanceof PendingConnection) {
			final PlayerModule playerModule = moduleManager.getPlayerModule();
			final BotPlayer botPlayer = playerModule.get(connection.getAddress().getHostString());
			final String name = ((PendingConnection) connection).getName(), lastNickname = botPlayer.getLastNickname();
			final int repings = botPlayer.getRepings(), reconnects = botPlayer.getReconnects() + 1;
			final long currentTimeMillis = System.currentTimeMillis();

			if (!lastNickname.equals(name) || (timesPing > 0 && (currentTimeMillis - botPlayer.getLastPing() < 550))
					|| currentTimeMillis - botPlayer.getLastConnection() < throttle) {
				botPlayer.setReconnects(0);
				botPlayer.setRepings(0);
				botPlayer.setLastNickname(name);
			} else {
				return (reconnects < timesConnect || repings < timesPing);
			}
		}

		return true;
	}

	int getTimesConnect() {
		return timesConnect;
	}
}
