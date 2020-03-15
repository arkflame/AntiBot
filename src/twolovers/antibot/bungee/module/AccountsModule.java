package twolovers.antibot.bungee.module;

import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import twolovers.antibot.bungee.instanceables.BotPlayer;
import twolovers.antibot.bungee.instanceables.Conditions;
import twolovers.antibot.bungee.utils.ConfigUtil;
import twolovers.antibot.shared.interfaces.PunishModule;

import java.util.*;

public class AccountsModule implements PunishModule {
	private final ModuleManager moduleManager;
	private Collection<String> punishCommands = new HashSet<>();
	private Conditions conditions;
	private boolean enabled = true;
	private int limit = 2;

	public AccountsModule(final ModuleManager moduleManager) {
		this.moduleManager = moduleManager;
	}

	@Override
	public String getName() {
		return "accounts";
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
			this.conditions = new Conditions(pps, cps, jps, false);
			this.limit = configYml.getInt(name + ".limit");
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
			final Collection<ProxiedPlayer> accounts = botPlayer.getAccounts();

			return accounts.size() + 1 >= limit;
		}

		return false;
	}

	@Override
	public Collection<String> getPunishCommands() {
		return punishCommands;
	}
}
