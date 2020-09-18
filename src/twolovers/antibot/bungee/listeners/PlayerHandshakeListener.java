package twolovers.antibot.bungee.listeners;

import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PlayerHandshakeEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import twolovers.antibot.bungee.instanceables.BotPlayer;
import twolovers.antibot.bungee.instanceables.Punish;
import twolovers.antibot.bungee.module.BlacklistModule;
import twolovers.antibot.bungee.module.ModuleManager;
import twolovers.antibot.bungee.module.PlayerModule;
import twolovers.antibot.bungee.module.RateLimitModule;

public class PlayerHandshakeListener implements Listener {
	private final Plugin plugin;
	private final ModuleManager moduleManager;

	public PlayerHandshakeListener(final Plugin plugin, final ModuleManager moduleManager) {
		this.plugin = plugin;
		this.moduleManager = moduleManager;
	}

	@EventHandler(priority = Byte.MIN_VALUE)
	public void onPlayerHandshake(final PlayerHandshakeEvent event) {
		final BlacklistModule blacklistModule = moduleManager.getBlacklistModule();
		final PlayerModule playerModule = moduleManager.getPlayerModule();
		final RateLimitModule rateLimitModule = moduleManager.getRateLimitModule();
		final PendingConnection connection = event.getConnection();
		final String ip = connection.getAddress().getHostString();
		final BotPlayer botPlayer = playerModule.get(ip);
		final int requestedProtocol = event.getHandshake().getRequestedProtocol();
		int currentPps = moduleManager.getCurrentPps();
		int currentCps = moduleManager.getCurrentCps();
		final int currentJps = moduleManager.getCurrentJps();
		final int lastPps = moduleManager.getLastPps();
		final int lastCps = moduleManager.getLastCps();
		final int lastJps = moduleManager.getLastJps();

		moduleManager.addIncoming();

		if (requestedProtocol == 1) {
			currentPps++;
		} else {
			currentCps++;
		}

		if (rateLimitModule.meet(botPlayer.getPPS(), botPlayer.getCPS(), botPlayer.getJPS(), 0, 0, 0)) {
			new Punish(plugin, moduleManager, "en", rateLimitModule, connection, event);
			blacklistModule.setBlacklisted(ip, true);
		} else if (blacklistModule.meet(currentPps, currentCps, currentJps, lastPps, lastCps, lastJps)
				&& blacklistModule.check(connection)) {
			new Punish(plugin, moduleManager, "en", blacklistModule, connection, event);
		}
	}
}
