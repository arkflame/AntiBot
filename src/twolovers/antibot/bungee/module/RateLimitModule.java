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
	private final String name = "ratelimit";
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
		return name;
	}

	@Override
	public final void reload(final ConfigUtil configUtil) {
		final Configuration configYml = configUtil.getConfiguration("%datafolder%/config.yml");
		final int pps = configYml.getInt(name + ".limits.pps", 0);
		final int cps = configYml.getInt(name + ".limits.cps", 0);
		final int jps = configYml.getInt(name + ".limits.jps", 0);

		enabled = configYml.getBoolean(name + ".enabled");
		punishCommands.clear();
		punishCommands.addAll(configYml.getStringList(name + ".commands"));
		conditions = new Conditions(pps, cps, jps, true);
		maxOnline = configYml.getInt(name + ".max_online");
		throttle = configYml.getInt(name + ".throttle");
	}

	@Override
	public boolean meet(final int pps, final int cps, final int jps) {
		return enabled;
	}

	@Override
	public boolean check(final Connection connection) {
		final PlayerModule playerModule = moduleManager.getPlayerModule();
		final BotPlayer botPlayer = playerModule.get(connection.getAddress().getHostString());
		final long lastConnection = botPlayer.getLastConnection();
		final int pps = botPlayer.getPPS();
		final int cps = botPlayer.getCPS();
		final int jps = botPlayer.getJPS();
		final boolean isThrottle = (cps == 0 && pps >= 0) ? false
				: System.currentTimeMillis() - lastConnection < throttle;

		return conditions.meet(pps, cps, jps, pps, cps, jps) || isThrottle || botPlayer.getPlayers().size() > maxOnline;
	}

	@Override
	public Collection<String> getPunishCommands() {
		return punishCommands;
	}
}
