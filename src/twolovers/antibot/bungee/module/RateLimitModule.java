package twolovers.antibot.bungee.module;

import java.util.Collection;
import java.util.HashSet;

import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.config.Configuration;
import twolovers.antibot.bungee.instanceables.BotPlayer;
import twolovers.antibot.bungee.instanceables.Conditions;
import twolovers.antibot.bungee.utils.ConfigUtil;
import twolovers.antibot.shared.interfaces.PunishModule;

public class RateLimitModule implements PunishModule {
	private final ModuleManager moduleManager;
	private Collection<String> punishCommands = new HashSet<>();
	private Conditions conditions;
	private boolean enabled = true;
	private int maxOnline = 3;
	private long throttle = 800;

	RateLimitModule(final ModuleManager moduleManager) {
		this.moduleManager = moduleManager;
	}

	@Override
	public String getName() {
		return "ratelimit";
	}

	@Override
	public final void reload(final ConfigUtil configUtil) {
		final Configuration configYml = configUtil.getConfiguration("%datafolder%/config.yml");

		if (configYml != null) {
			final String name = getName();
			final int pps = configYml.getInt(name + ".conditions.pps", 0);
			final int cps = configYml.getInt(name + ".conditions.cps", 0);
			final int jps = configYml.getInt(name + ".conditions.jps", 0);

			this.enabled = configYml.getBoolean(name + ".enabled");
			this.punishCommands.clear();
			this.punishCommands.addAll(configYml.getStringList(name + ".commands"));
			this.conditions = new Conditions(pps, cps, jps, true);
			this.maxOnline = configYml.getInt(name + ".max_online");
			this.throttle = configYml.getInt(name + ".throttle");
		}
	}

	@Override
	public boolean meet(final int pps, final int cps, final int jps) {
		return this.enabled;
	}

	@Override
	public boolean check(final Connection connection) {
		final PlayerModule playerModule = moduleManager.getPlayerModule();
		final BotPlayer botPlayer = playerModule.get(connection.getAddress().getHostString());
		final long lastConnection;
		final int pps = botPlayer.getPPS();
		final int cps = botPlayer.getCPS();
		final int jps = botPlayer.getJPS();

		if (cps > 0) {
			lastConnection = botPlayer.getLastConnection();
		} else {
			lastConnection = 0;
		}

		return conditions.meet(pps, cps, jps, moduleManager.getLastPPS(), moduleManager.getLastCPS(),
				moduleManager.getLastJPS()) || System.currentTimeMillis() - lastConnection < throttle
				|| botPlayer.getPlayers().size() > maxOnline;
	}

	@Override
	public Collection<String> getPunishCommands() {
		return punishCommands;
	}
}
