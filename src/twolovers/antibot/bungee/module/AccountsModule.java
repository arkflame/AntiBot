package twolovers.antibot.bungee.module;

import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.config.Configuration;
import twolovers.antibot.bungee.instanceables.BotPlayer;
import twolovers.antibot.bungee.utils.ConfigUtil;
import twolovers.antibot.shared.extendables.PunishableModule;

public class AccountsModule extends PunishableModule {
    private final ModuleManager moduleManager;
    private int limit = 2;

    public AccountsModule(final ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
    }

    @Override
    public final void reload(final ConfigUtil configUtil) {
        super.name = "accounts";
        super.reload(configUtil);

        final Configuration configYml = configUtil.getConfiguration("%datafolder%/config.yml");

        punishCommands.clear();
        punishCommands.addAll(configYml.getStringList(name + ".commands"));
        limit = configYml.getInt(name + ".limit", limit);
    }

    public boolean check(final Connection connection) {
        if (!(connection instanceof PendingConnection)) {
            return false;
        }

        final PlayerModule playerModule = moduleManager.getPlayerModule();
        final BotPlayer botPlayer = playerModule.get(((PendingConnection) connection).getVirtualHost().getHostString());

        return botPlayer.getTotalAccounts() >= limit;
    }
}
