package twolovers.antibot.bungee.module;

import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.config.Configuration;
import twolovers.antibot.bungee.instanceables.Conditions;
import twolovers.antibot.bungee.utils.ConfigUtil;
import twolovers.antibot.shared.interfaces.IPunishModule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class BlacklistModule implements IPunishModule {
	private static final String NAME = "blacklist";
	private static final String BLACKLIST_PATH = "%datafolder%/blacklist.yml";
	private final ModuleManager moduleManager;
	private Collection<String> blacklist = new HashSet<>(), punishCommands = new HashSet<>();
	private Conditions conditions;
	private boolean enabled = true;

	BlacklistModule(final ModuleManager moduleManager) {
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

		load(configUtil);
	}

	public void setBlacklisted(final String address, final boolean blacklist) {
		if (blacklist) {
			moduleManager.getWhitelistModule().setWhitelisted(address, false);

			try {
				moduleManager.getRuntimeModule().addBlacklisted(address);
			} catch (IOException e) {
				e.printStackTrace();
			}

			this.blacklist.add(address);
		} else {
			this.blacklist.remove(address);
		}
	}

	final int getSize() {
		return blacklist.size();
	}

	public void save(final ConfigUtil configUtil) {
		final Configuration blacklistYml = configUtil.getConfiguration(BLACKLIST_PATH);

		if (blacklistYml != null) {
			blacklistYml.set("", new ArrayList<>(blacklist));
			configUtil.saveConfiguration(blacklistYml, BLACKLIST_PATH);
		}
	}

	public void load(final ConfigUtil configUtil) {
		final Configuration blacklistYml = configUtil.getConfiguration(BLACKLIST_PATH);

		this.blacklist.clear();
		this.blacklist.addAll(blacklistYml.getStringList(""));
	}

	public boolean meet(int pps, int cps, int jps) {
		return this.enabled && conditions.meet(pps, cps, jps, moduleManager.getLastPPS(), moduleManager.getLastCPS(),
				moduleManager.getLastJPS());
	}

	public boolean check(final Connection connection) {
		return blacklist.contains(connection.getAddress().getHostString());
	}

	public boolean meetCheck(int pps, int cps, int jps, Connection connection) {
		return meet(pps, cps, jps) && check(connection);
	}

	@Override
	public Collection<String> getPunishCommands() {
		return punishCommands;
	}

	public boolean isBlacklisted(final String ip) {
		return this.blacklist.contains(ip);
	}

	public Collection<String> getBlacklist() {
		return this.blacklist;
	}
}
