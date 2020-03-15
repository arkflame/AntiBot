package twolovers.antibot.bungee.listeners;

import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import twolovers.antibot.bungee.instanceables.BotPlayer;
import twolovers.antibot.bungee.instanceables.Punish;
import twolovers.antibot.bungee.module.BlacklistModule;
import twolovers.antibot.bungee.module.ModuleManager;
import twolovers.antibot.bungee.module.PlayerModule;
import twolovers.antibot.bungee.module.RateLimitModule;

public class ProxyPingListener implements Listener {
	private final Plugin plugin;
	private final ModuleManager moduleManager;
	private final RateLimitModule rateLimitModule;
	private final BlacklistModule blacklistModule;

	public ProxyPingListener(final Plugin plugin, final ModuleManager moduleManager) {
		this.plugin = plugin;
		this.moduleManager = moduleManager;
		this.rateLimitModule = moduleManager.getRateLimitModule();
		this.blacklistModule = moduleManager.getBlacklistModule();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onProxyPing(final ProxyPingEvent event) {
		if (event.getResponse() != null) {
			final PlayerModule playerModule = moduleManager.getPlayerModule();
			final PendingConnection connection = event.getConnection();
			final String ip = connection.getAddress().getHostString();
			final long currentTimeMillis = System.currentTimeMillis();
			final BotPlayer botPlayer = playerModule.get(ip);

			botPlayer.setPPS(botPlayer.getPPS() + 1);

			final int currentPPS = moduleManager.getCurrentPPS() + 1;
			final int currentCPS = moduleManager.getCurrentCPS();
			final int currentJPS = moduleManager.getCurrentJPS();

			moduleManager.setCurrentPPS(currentPPS);

			if (rateLimitModule.meet(currentPPS, currentCPS, currentJPS) && rateLimitModule.check(connection)) {
				new Punish(plugin, moduleManager, "en", rateLimitModule.getPunishCommands(), connection, event,
						"Ratelimit");

				blacklistModule.setBlacklisted(ip, true);
			} else if (blacklistModule.meet(currentPPS, currentCPS, currentJPS) && blacklistModule.check(connection)) {
				new Punish(plugin, moduleManager, "en", rateLimitModule.getPunishCommands(), connection, event,
						"Blacklist");
			} else {
				if (botPlayer.getPlayers().size() < 1) {
					playerModule.setOffline(botPlayer);
				}
			}

			botPlayer.setLastPing(currentTimeMillis);
		}
	}
}
