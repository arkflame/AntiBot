package twolovers.antibot.bungee.listeners;

import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PlayerHandshakeEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.protocol.packet.Handshake;
import twolovers.antibot.bungee.instanceables.Punish;
import twolovers.antibot.bungee.module.BlacklistModule;
import twolovers.antibot.bungee.module.ModuleManager;
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
		try {
			final PendingConnection connection = event.getConnection();

			if (connection.isConnected()) {
				final Handshake handshake = event.getHandshake();
				final int requestedProtocol = handshake.getRequestedProtocol();

				switch (requestedProtocol) {
					case 1: {
						final BlacklistModule blacklistModule = moduleManager.getBlacklistModule();
						final RateLimitModule rateLimitModule = moduleManager.getRateLimitModule();
						final String ip = connection.getAddress().getHostString();
						final int currentPPS = moduleManager.getCurrentPPS() + 1;
						final int currentCPS = moduleManager.getCurrentCPS();
						final int currentJPS = moduleManager.getCurrentJPS();

						if (rateLimitModule.meet(currentPPS, currentCPS, currentJPS)
								&& rateLimitModule.check(connection)) {
							new Punish(plugin, moduleManager, "en", rateLimitModule, connection, event);

							blacklistModule.setBlacklisted(ip, true);
						} else if (blacklistModule.meet(currentPPS, currentCPS, currentJPS)
								&& blacklistModule.check(connection)) {
							new Punish(plugin, moduleManager, "en", blacklistModule, connection, event);
						}

						break;
					}
				}
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}
