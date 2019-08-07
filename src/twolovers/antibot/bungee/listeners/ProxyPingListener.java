package twolovers.antibot.bungee.listeners;

import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import twolovers.antibot.bungee.managers.ModuleManager;
import twolovers.antibot.bungee.modules.BlacklistModule;
import twolovers.antibot.bungee.modules.NotificationsModule;
import twolovers.antibot.bungee.modules.RateLimitModule;

public class ProxyPingListener implements Listener {
	private final ModuleManager moduleManager;
	private final RateLimitModule rateLimitModule;
	private final BlacklistModule blacklistModule;
	private final NotificationsModule notificationsModule;

	public ProxyPingListener(ModuleManager moduleManager) {
		this.moduleManager = moduleManager;
		this.rateLimitModule = moduleManager.getRateLimitModule();
		this.blacklistModule = moduleManager.getBlacklistModule();
		this.notificationsModule = moduleManager.getNotificationsModule();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onProxyPing(final ProxyPingEvent event) {
		final Connection connection = event.getConnection();

		if (event.getResponse() != null) {
			final String ip = connection.getAddress().getAddress().getHostAddress();
			final long currentTimeMillis = System.currentTimeMillis();

			moduleManager.addPPS(ip, 1);

			if (rateLimitModule.isCondition(ip)) {
				if (connection.isConnected())
					connection.disconnect();

				blacklistModule.setBlacklisted(ip, true);
				notificationsModule.sendNotification("RateLimit", "PPS", ip);
			} else if (blacklistModule.isCondition(ip)) {
				if (connection.isConnected())
					connection.disconnect();

				notificationsModule.sendNotification("Blacklist", "PPS", ip);
			}

			moduleManager.setLastPing(ip, currentTimeMillis);
		}
	}
}
