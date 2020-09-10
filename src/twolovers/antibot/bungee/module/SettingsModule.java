package twolovers.antibot.bungee.module;

import net.md_5.bungee.config.Configuration;
import twolovers.antibot.bungee.instanceables.BotPlayer;
import twolovers.antibot.bungee.instanceables.Conditions;
import twolovers.antibot.bungee.utils.ConfigUtil;
import twolovers.antibot.shared.extendables.PunishableModule;

import java.util.Collection;
import java.util.HashSet;

public class SettingsModule extends PunishableModule {
	private static final String NAME = "settings";
	private final Collection<BotPlayer> pending = new HashSet<>();
	private int delay = 5000;

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
