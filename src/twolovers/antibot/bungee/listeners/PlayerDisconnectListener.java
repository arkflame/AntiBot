package twolovers.antibot.bungee.listeners;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import twolovers.antibot.bungee.instanceables.BotPlayer;
import twolovers.antibot.bungee.module.*;

public class PlayerDisconnectListener implements Listener {
    private final ModuleManager moduleManager;

    public PlayerDisconnectListener(final ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
    }

    @EventHandler
    public void onPlayerDisconnect(final PlayerDisconnectEvent event) {
        final NotificationsModule notificationsModule = moduleManager.getNotificationsModule();
        final PlayerModule playerModule = moduleManager.getPlayerModule();
        final SettingsModule settingsModule = moduleManager.getSettingsModule();
        final WhitelistModule whitelistModule = moduleManager.getWhitelistModule();
        final ProxiedPlayer proxiedPlayer = event.getPlayer();
        final String ip = proxiedPlayer.getPendingConnection().getVirtualHost().getHostString();
        final BotPlayer botPlayer = playerModule.get(ip);
        final long currentTime = System.currentTimeMillis();

        if (proxiedPlayer.getPing() < 500 && (!whitelistModule.isRequireSwitch() || botPlayer.getSwitchs() > 1)
                && currentTime - botPlayer.getLastConnection() >= whitelistModule.getTimeWhitelist()) {
            whitelistModule.setWhitelisted(ip, true);
        }

        botPlayer.removeAccount(proxiedPlayer.getName());
        botPlayer.resetSwitchs();
        notificationsModule.setNotifications(proxiedPlayer, false);
        settingsModule.removePending(botPlayer);

        if (botPlayer.getAccounts().isEmpty()) {
            botPlayer.setSettings(false);
            playerModule.setOffline(botPlayer);
        }
    }
}
