package twolovers.antibot.bungee.module;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.config.Configuration;
import twolovers.antibot.bungee.utils.ConfigUtil;
import twolovers.antibot.bungee.utils.Incoming;
import twolovers.antibot.shared.extendables.PunishableModule;

public class WhitelistModule extends PunishableModule {
	private static final String WHITELIST_PATH = "%datafolder%/whitelist.yml";
	private final ModuleManager moduleManager;
	private final Collection<String> whitelist = new HashSet<>();
	private long lastLockout = 0;
	private int timeWhitelist = 15000;
	private int timeLockout = 20000;
	private boolean requireSwitch = true;

	WhitelistModule(final ModuleManager moduleManager) {
		this.moduleManager = moduleManager;
	}

	@Override
	public final void reload(final ConfigUtil configUtil) {
		super.name = "whitelist";
		super.reload(configUtil);

		final Configuration configYml = configUtil.getConfiguration("%datafolder%/config.yml");

		punishCommands.clear();
		punishCommands.addAll(configYml.getStringList(name + ".commands"));
		requireSwitch = configYml.getBoolean(name + ".switch", requireSwitch);
		timeWhitelist = configYml.getInt(name + ".time.whitelist", timeWhitelist);
		timeLockout = configYml.getInt(name + ".time.lockout", timeLockout);
		load(configUtil);
	}

	public final void load(final ConfigUtil configUtil) {
		final Configuration whitelistYml = configUtil.getConfiguration(WHITELIST_PATH);

		this.whitelist.clear();

		if (whitelistYml != null) {
			this.whitelist.addAll(whitelistYml.getStringList(""));
		}
	}

	public final void setWhitelisted(final String ip, final boolean input) {
		if (input) {
			moduleManager.getBlacklistModule().setBlacklisted(ip, false);
			whitelist.add(ip);
		} else {
			whitelist.remove(ip);
		}
	}

	final int getSize() {
		return whitelist.size();
	}

	public final void save(final ConfigUtil configUtil) {
		final Configuration whitelistYml = configUtil.getConfiguration(WHITELIST_PATH);

		if (whitelistYml != null) {
			whitelistYml.set("", new ArrayList<>(whitelist));
			configUtil.saveConfiguration(whitelistYml, WHITELIST_PATH);
		}
	}

	@Override
	public final boolean meet(final Incoming ...incoming) {
		return this.enabled && (thresholds.meet(incoming)
				|| System.currentTimeMillis() - this.lastLockout < this.timeLockout);
	}

	public final boolean check(final Connection connection) {
		return whitelist.contains(connection.getAddress().getHostString());
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
