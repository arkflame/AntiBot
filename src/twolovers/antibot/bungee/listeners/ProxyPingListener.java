package twolovers.antibot.bungee.listeners;

import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import twolovers.antibot.bungee.instanceables.BotPlayer;
import twolovers.antibot.bungee.instanceables.Punish;
import twolovers.antibot.bungee.module.*;
import twolovers.antibot.bungee.utils.Incoming;

public class ProxyPingListener implements Listener {
    private final ModuleManager moduleManager;
    private final BlacklistModule blacklistModule;
    private final PlayerModule playerModule;
    private final RateLimitModule rateLimitModule;
    private final WhitelistModule whitelistModule;

    public ProxyPingListener(final ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
        this.blacklistModule = moduleManager.getBlacklistModule();
        this.playerModule = moduleManager.getPlayerModule();
        this.rateLimitModule = moduleManager.getRateLimitModule();
        this.whitelistModule = moduleManager.getWhitelistModule();
    }

    @EventHandler(priority = Byte.MIN_VALUE)
    public void onProxyPing(final ProxyPingEvent event) {
        if (event.getResponse() == null || event instanceof Cancellable && ((Cancellable) event).isCancelled()) {
            return;
        }

        final Connection connection = event.getConnection();
        final String locale = moduleManager.getDefaultLanguage(); // Can't get locale on PreLogin.
        final String ip = connection.getAddress().getHostString();
        final BotPlayer botPlayer = playerModule.get(ip);
        final long currentTimeMillis = System.currentTimeMillis();
        final CounterModule counterModule = moduleManager.getCounterModule();
        final Incoming current = counterModule.getCurrent();
        final Incoming last = counterModule.getLast();

        botPlayer.setLastPing(currentTimeMillis);
        if (whitelistModule.meet(current, last)) {
            new Punish(moduleManager, locale, whitelistModule, connection, event);
            whitelistModule.setLastLockout(currentTimeMillis);
            return;
        }

        if (blacklistModule.meet(current, last) && blacklistModule.check(connection)) {
            new Punish(moduleManager, locale, blacklistModule, connection, event);
            return;
        }

        if (rateLimitModule.meet(botPlayer.getIncoming())) {
            new Punish(moduleManager, locale, rateLimitModule, connection, event);
            blacklistModule.setBlacklisted(ip, true);
        }
    }
}
