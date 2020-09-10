package twolovers.antibot.bungee.listeners;

import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import twolovers.antibot.bungee.instanceables.BotPlayer;
import twolovers.antibot.bungee.instanceables.Punish;
import twolovers.antibot.bungee.module.BlacklistModule;
import twolovers.antibot.bungee.module.ModuleManager;
import twolovers.antibot.bungee.module.PlayerModule;
import twolovers.antibot.bungee.module.RateLimitModule;

public class ProxyPingListener implements Listener {
	private final Plugin plugin;
	private final ModuleManager moduleManager;

	public ProxyPingListener(final Plugin plugin, final ModuleManager moduleManager) {
		this.plugin = plugin;
		this.moduleManager = moduleManager;
	}

	@EventHandler(priority = Byte.MIN_VALUE)
	public void onProxyPing(final ProxyPingEvent event) {
		if (event.getResponse() != null) {
			final BlacklistModule blacklistModule = moduleManager.getBlacklistModule();
			final RateLimitModule rateLimitModule = moduleManager.getRateLimitModule();
			final PlayerModule playerModule = moduleManager.getPlayerModule();
			final Connection connection = event.getConnection();
			final String ip = connection.getAddress().getHostString();
			final long currentTimeMillis = System.currentTimeMillis();
			final BotPlayer botPlayer = playerModule.get(ip);
			final int currentPps = moduleManager.getCurrentPps() + 1;
			final int currentCps = moduleManager.getCurrentCps();
			final int currentJps = moduleManager.getCurrentJps();
			final int lastPps = moduleManager.getLastPps();
			final int lastCps = moduleManager.getLastCps();
			final int lastJps = moduleManager.getLastJps();

			botPlayer.setPPS(botPlayer.getPPS() + 1);
			moduleManager.setCurrentPPS(currentPps);

			if (rateLimitModule.meet(botPlayer.getPPS(), botPlayer.getCPS(), botPlayer.getJPS(), 0, 0, 0)) {
				new Punish(plugin, moduleManager, "en", rateLimitModule, connection, event);

				blacklistModule.setBlacklisted(ip, true);
			} else if (blacklistModule.meet(currentPps, currentCps, currentJps, lastPps, lastCps, lastJps)
					&& blacklistModule.check(connection)) {
				new Punish(plugin, moduleManager, "en", blacklistModule, connection, event);
			} else if (botPlayer.getAccounts().isEmpty()) {
				playerModule.setOffline(botPlayer);
			}

			botPlayer.setRepings(botPlayer.getRepings() + 1);
			botPlayer.setLastPing(currentTimeMillis);
		}
	}
}
