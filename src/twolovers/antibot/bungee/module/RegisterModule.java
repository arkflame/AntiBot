package twolovers.antibot.bungee.module;

import java.util.Collection;
import java.util.HashSet;

import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.config.Configuration;
import twolovers.antibot.bungee.instanceables.Conditions;
import twolovers.antibot.bungee.utils.ConfigUtil;
import twolovers.antibot.shared.interfaces.IPunishModule;

public class RegisterModule implements IPunishModule {
	private final String name = "register";
	private final ModuleManager moduleManager;
	private Collection<String> punishCommands = new HashSet<>();
	private Collection<String> authCommands = new HashSet<>();
	private Conditions conditions;
	private boolean enabled = true;
	private String lastRegisterIp = "", lastRegisterCommand = "/reg A";

	public RegisterModule(final ModuleManager moduleManager) {
		this.moduleManager = moduleManager;
	}

	@Override
	public final String getName() {
		return name;
	}

	@Override
	public final void reload(final ConfigUtil configUtil) {
		final Configuration configYml = configUtil.getConfiguration("%datafolder%/config.yml");
		final int pps = configYml.getInt(name + ".conditions.pps", 0);
		final int cps = configYml.getInt(name + ".conditions.cps", 0);
		final int jps = configYml.getInt(name + ".conditions.jps", 0);

		enabled = configYml.getBoolean(name + ".enabled", enabled);
		punishCommands.clear();
		punishCommands.addAll(configYml.getStringList(name + ".commands"));
		authCommands.clear();
		authCommands.addAll(configYml.getStringList(name + ".auth-commands"));
		conditions = new Conditions(pps, cps, jps, false);
	}

	public final void setLastRegisterCommand(final String ip, final String command) {
		lastRegisterIp = ip;
		lastRegisterCommand = command;
	}

	@Override
	public final boolean meet(final int pps, final int cps, final int jps) {
		return this.enabled && conditions.meet(pps, cps, jps, moduleManager.getLastPPS(), moduleManager.getLastCPS(),
				moduleManager.getLastJPS());
	}

	@Override
	public final boolean check(final Connection connection) {
		return true;
	}

	public final boolean check(final Connection connection, final String command) {
		final String ip = connection.getAddress().getHostString();

		for (final String cmds : authCommands) {
			if (command.startsWith(cmds)) {
				return command.split(" ").length > 1 && !ip.equals(lastRegisterIp)
						&& command.equals(lastRegisterCommand);
			}
		}

		return false;
	}

	@Override
	public final Collection<String> getPunishCommands() {
		return punishCommands;
	}
}
