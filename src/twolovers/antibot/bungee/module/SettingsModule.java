package twolovers.antibot.bungee.module;

import java.util.Collection;
import java.util.HashSet;

import net.md_5.bungee.config.Configuration;
import twolovers.antibot.bungee.instanceables.BotPlayer;
import twolovers.antibot.bungee.utils.ConfigUtil;
import twolovers.antibot.shared.extendables.PunishableModule;

public class SettingsModule extends PunishableModule {
	private final Collection<BotPlayer> pending = new HashSet<>();
	private int delay = 5000;
	private boolean switching = false;

	@Override
	public final void reload(final ConfigUtil configUtil) {
		super.name = "settings";
		super.reload(configUtil);

		final Configuration configYml = configUtil.getConfiguration("%datafolder%/config.yml");

		punishCommands.clear();
		punishCommands.addAll(configYml.getStringList(name + ".commands"));
		delay = configYml.getInt(name + ".delay", delay);
		switching = configYml.getBoolean(name + ".switching", switching);
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

	public boolean isSwitching() {
		return switching;
	}
}
