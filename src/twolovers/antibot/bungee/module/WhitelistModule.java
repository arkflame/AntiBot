package twolovers.antibot.bungee.module;

import java.util.Collection;
import java.util.HashSet;

import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.config.Configuration;
import twolovers.antibot.bungee.instanceables.Conditions;
import twolovers.antibot.bungee.utils.ConfigUtil;
import twolovers.antibot.shared.interfaces.PunishModule;

public class WhitelistModule implements PunishModule {
	private final ModuleManager moduleManager;
	private Collection<String> whitelist = new HashSet<>();
	private Collection<String> punishCommands = new HashSet<>();
	private Conditions conditions;
	private long lastLockout = 0;
	private int timeWhitelist, timeLockout;
	private boolean enabled, requireSwitch;

	WhitelistModule(final ModuleManager moduleManager) {
		this.moduleManager = moduleManager;
	}

	@Override
	public String getName() {
		return "whitelist";
	}

	@Override
	public final void reload(final ConfigUtil configUtil) {
		final Configuration configYml = configUtil.getConfiguration("%datafolder%/config.yml");

		if (configYml != null) {
			final String name = getName();
			final int pps = configYml.getInt(name + ".conditions.pps", 0);
			final int cps = configYml.getInt(name + ".conditions.cps", 0);
			final int jps = configYml.getInt(name + ".conditions.jps", 0);

			this.enabled = configYml.getBoolean(name + ".enabled", true);
			this.punishCommands.clear();
			this.punishCommands.addAll(configYml.getStringList(name + ".commands"));
			this.conditions = new Conditions(pps, cps, jps, false);
			this.requireSwitch = configYml.getBoolean(name + ".switch", true);
			this.timeWhitelist = configYml.getInt(name + ".time.whitelist", 30000);
			this.timeLockout = configYml.getInt(name + ".time.lockout", 30000);
		}

		this.load(configUtil);
	}

	public final void load(final ConfigUtil configUtil) {
		final Configuration whitelistYml = configUtil.getConfiguration("%datafolder%/whitelist.yml");

		this.whitelist.clear();

		if (whitelistYml != null)
			this.whitelist.addAll(whitelistYml.getStringList(""));
	}

	public final void setWhitelisted(final String ip, final boolean input) {
		if (input) {
			moduleManager.getBlacklistModule().setBlacklisted(ip, false);
			whitelist.add(ip);
		} else
			whitelist.remove(ip);
	}

	final int getSize() {
		return whitelist.size();
	}

	public final void save(final ConfigUtil configUtil) {
		final Configuration whitelistYml = configUtil.getConfiguration("%datafolder%/whitelist.yml");

		if (whitelistYml != null) {
			whitelistYml.set("", whitelist);
			configUtil.saveConfiguration(whitelistYml, "%datafolder%/whitelist.yml");
		}
	}

	@Override
	public final boolean meet(int pps, int cps, int jps) {
		return this.enabled && (conditions.meet(pps, cps, jps, moduleManager.getLastPPS(), moduleManager.getLastCPS(),
				moduleManager.getLastJPS()) || System.currentTimeMillis() - this.lastLockout < this.timeLockout);
	}

	@Override
	public final boolean check(final Connection connection) {
		return whitelist.contains(connection.getAddress().getHostString());
	}

	@Override
	public final Collection<String> getPunishCommands() {
		return punishCommands;
	}

	public boolean isRequireSwitch() {
		return requireSwitch;
	}

	public int getTimeWhitelist() {
		return timeWhitelist;
	}

	public void setLastLockout(final long lastLockout) {
		if (System.currentTimeMillis() - this.lastLockout >= this.timeLockout) {
			this.lastLockout = lastLockout;
		}
	}
}
