package twolovers.antibot.bungee.module;

import net.md_5.bungee.config.Configuration;
import twolovers.antibot.bungee.instanceables.BotPlayer;
import twolovers.antibot.bungee.instanceables.Conditions;
import twolovers.antibot.bungee.utils.ConfigUtil;
import twolovers.antibot.shared.interfaces.IPunishModule;

import java.util.Collection;
import java.util.HashSet;

public class SettingsModule implements IPunishModule {
	private static final String NAME = "settings";
	private final ModuleManager moduleManager;
	private final Collection<String> punishCommands = new HashSet<>();
	private final Collection<BotPlayer> pending = new HashSet<>();
	private Conditions conditions;
	private int delay = 5000;
	private boolean enabled = true;

	public SettingsModule(final ModuleManager moduleManager) {
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
		delay = configYml.getInt(NAME + ".delay", delay);
	}

	public final boolean meet(final int pps, final int cps, final int jps) {
		return enabled && conditions.meet(pps, cps, jps, moduleManager.getLastPPS(), moduleManager.getLastCPS(),
				moduleManager.getLastJPS());
	}

	@Override
	public final Collection<String> getPunishCommands() {
		return punishCommands;
	}

	public Collection<BotPlayer> getPending() {
		return pending;
	}

	public void addPending(final BotPlayer botPlayer) {
		pending.add(botPlayer);
	}

	public void removePending(final BotPlayer botPlayer) {
		pending.remove(botPlayer);
	}

	public long getDelay() {
		return delay;
	}
}
