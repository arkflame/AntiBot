package twolovers.antibot.bungee.module;

import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.config.Configuration;
import twolovers.antibot.bungee.utils.ConfigUtil;
import twolovers.antibot.shared.extendables.PunishableModule;

import java.util.Collection;
import java.util.HashSet;

public class PasswordModule extends PunishableModule {
    private Collection<String> authCommands = new HashSet<>();
    private String lastAddress = "";
    private String lastPassword = "";

    @Override
    public final void reload(final ConfigUtil configUtil) {
        super.name = "password";
        super.reload(configUtil);

        final Configuration configYml = configUtil.getConfiguration("%datafolder%/config.yml");

        punishCommands.clear();
        punishCommands.addAll(configYml.getStringList(name + ".commands"));
        authCommands.clear();
        authCommands.addAll(configYml.getStringList(name + ".auth_commands"));
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
                if (!command.startsWith(authCommand)) continue;

                final String[] splittedCommand = command.split(" ");
                final String password = splittedCommand[1];

                return !address.equals(lastAddress) && password.equals(lastPassword);
            }
        }

        return false;
    }
}
