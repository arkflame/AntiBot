package twolovers.antibot.bungee.module;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.config.Configuration;
import twolovers.antibot.bungee.instanceables.Conditions;
import twolovers.antibot.bungee.utils.ConfigUtil;
import twolovers.antibot.shared.interfaces.IPunishModule;

public class WhitelistModule implements IPunishModule {
	private final String name = "whitelist";
	private final ModuleManager moduleManager;
	private final Collection<String> whitelist = new HashSet<>(), punishCommands = new HashSet<>();
	private Conditions conditions;
	private long lastLockout = 0;
	private int timeWhitelist = 15000, timeLockout = 20000;
	private boolean enabled = true, requireSwitch = true;

	WhitelistModule(final ModuleManager moduleManager) {
		this.moduleManager = moduleManager;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public final void reload(final ConfigUtil configUtil) {
		final Configuration configYml = configUtil.getConfiguration("%datafolder%/config.yml");
		final int pps = configYml.getInt(name + ".conditions.pps", 0);
		final int cps = configYml.getInt(name + ".conditions.cps", 0);
		final int jps = configYml.getInt(name + ".conditions.jps", 0);

		enabled = configYml.getBoolean(name + ".enabled", true);
		punishCommands.clear();
		punishCommands.addAll(configYml.getStringList(name + ".commands"));
		conditions = new Conditions(pps, cps, jps, false);
		requireSwitch = configYml.getBoolean(name + ".switch", requireSwitch);
		timeWhitelist = configYml.getInt(name + ".time.whitelist", timeWhitelist);
		timeLockout = configYml.getInt(name + ".time.lockout", timeLockout);
		load(configUtil);
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
			whitelistYml.set("", new ArrayList<>(whitelist));
			configUtil.saveConfiguration(whitelistYml, "%datafolder%/whitelist.yml");
		}
	}

	@Override
	public final boolean meet(final int pps, final int cps, final int jps) {
		return this.enabled && (conditions.meet(pps, cps, jps, moduleManager.getLastPPS(), moduleManager.getLastCPS(),
				moduleManager.getLastJPS()) || System.currentTimeMillis() - this.lastLockout < this.timeLockout);
	}

	@Override
	public final boolean check(final Connection connection) {
		return this.enabled && whitelist.contains(connection.getAddress().getHostString());
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

	public boolean isWhitelisted(final String ip) {
		return this.whitelist.contains(ip);
	}

	public Collection<String> getWhitelist() {
		return this.whitelist;
	}
}
