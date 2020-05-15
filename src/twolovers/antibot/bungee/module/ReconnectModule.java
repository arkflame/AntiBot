package twolovers.antibot.bungee.module;

import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.config.Configuration;
import twolovers.antibot.bungee.instanceables.BotPlayer;
import twolovers.antibot.bungee.instanceables.Conditions;
import twolovers.antibot.bungee.utils.ConfigUtil;
import twolovers.antibot.shared.interfaces.PunishModule;

import java.util.Collection;
import java.util.HashSet;

public class ReconnectModule implements PunishModule {
	private final String name = "reconnect";
	private final ModuleManager moduleManager;
	private Collection<String> punishCommands = new HashSet<>();
	private Conditions conditions;
	private boolean enabled;
	private int timesPing, timesConnect;
	private long throttle;

	ReconnectModule(final ModuleManager moduleManager) {
		this.moduleManager = moduleManager;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public final void reload(final ConfigUtil configUtil) {
		final Configuration configYml = configUtil.getConfiguration("%datafolder%/config.yml");

		if (configYml != null) {
			final int pps = configYml.getInt(name + ".conditions.pps", 0);
			final int cps = configYml.getInt(name + ".conditions.cps", 0);
			final int jps = configYml.getInt(name + ".conditions.jps", 0);

			this.enabled = configYml.getBoolean(name + ".enabled", true);
			this.punishCommands.clear();
			this.punishCommands.addAll(configYml.getStringList(name + ".commands"));
			this.conditions = new Conditions(pps, cps, jps, false);
			this.timesPing = configYml.getInt(name + ".times.ping", 2);
			this.timesConnect = configYml.getInt(name + ".times.connect", 3);
			this.throttle = configYml.getLong(name + ".throttle", 1000);
		}
	}

	@Override
	public boolean meet(final int pps, final int cps, final int jps) {
		return this.enabled && conditions.meet(pps, cps, jps, moduleManager.getLastPPS(), moduleManager.getLastCPS(),
				moduleManager.getLastJPS());
	}

	@Override
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

	@Override
	public Collection<String> getPunishCommands() {
		return punishCommands;
	}

	int getTimesConnect() {
		return timesConnect;
	}
}
