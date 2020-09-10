package twolovers.antibot.bungee.module;

import java.util.Collection;
import java.util.HashSet;

import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.config.Configuration;
import twolovers.antibot.bungee.instanceables.Conditions;
import twolovers.antibot.bungee.utils.ConfigUtil;
import twolovers.antibot.shared.extendables.PunishableModule;

public class RegisterModule extends PunishableModule {
	private static final String NAME = "register";
	private Collection<String> authCommands = new HashSet<>();
	private String lastAddress = "";
	private String lastPassword = "";

	@Override
	public final String getName() {
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
		authCommands.clear();
		authCommands.addAll(configYml.getStringList(NAME + ".auth_commands"));
		conditions = new Conditions(pps, cps, jps, false);
	}

	public final void setLastValues(final String address, final String command) {
		if (command.contains(" ")) {
			final String[] splittedCommand = command.split(" ");
			final String password = splittedCommand[1];

			lastAddress = address;
			lastPassword = password;
		}
	}

	public final boolean check(final Connection connection, final String command) {
		final String address = connection.getAddress().getHostString();

		if (command.contains(" ")) {
			for (final String authCommand : authCommands) {
				if (command.startsWith(authCommand)) {
					final String[] splittedCommand = command.split(" ");
					final String password = splittedCommand[1];

					return !address.equals(lastAddress) && password.equals(lastPassword);
				}
			}
		}

		return false;
	}
}
