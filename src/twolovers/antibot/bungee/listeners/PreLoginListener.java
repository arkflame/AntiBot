package twolovers.antibot.bungee.listeners;

import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import twolovers.antibot.bungee.instanceables.BotPlayer;
import twolovers.antibot.bungee.instanceables.Punish;
import twolovers.antibot.bungee.module.*;

public class PreLoginListener implements Listener {
	private final Plugin plugin;
	private final ModuleManager moduleManager;

	public PreLoginListener(final Plugin plugin, final ModuleManager moduleManager) {
		this.plugin = plugin;
		this.moduleManager = moduleManager;
	}

	@EventHandler(priority = Byte.MIN_VALUE)
	public void onPreLogin(final PreLoginEvent event) {
		if (!event.isCancelled()) {
			try {
				final AccountsModule accountsModule = moduleManager.getAccountsModule();
				final BlacklistModule blacklistModule = moduleManager.getBlacklistModule();
				final NicknameModule nicknameModule = moduleManager.getNicknameModule();
				final PlayerModule playerModule = moduleManager.getPlayerModule();
				final RateLimitModule rateLimitModule = moduleManager.getRateLimitModule();
				final ReconnectModule reconnectModule = moduleManager.getReconnectModule();
				final WhitelistModule whitelistModule = moduleManager.getWhitelistModule();
				final PendingConnection connection = event.getConnection();

				if (!whitelistModule.check(connection)) {
					final String name = connection.getName();
					final String locale = "en", // Cant get locale on prelogin.
							ip = connection.getAddress().getHostString();
					final BotPlayer botPlayer = playerModule.get(ip);
					final long currentTimeMillis = System.currentTimeMillis();
					final int currentPPS = moduleManager.getCurrentPPS();
					final int currentCPS = moduleManager.getCurrentCPS() + 1;
					final int currentJPS = moduleManager.getCurrentJPS();

					botPlayer.setLastNickname(name);
					botPlayer.setCPS(botPlayer.getCPS() + 1);
					moduleManager.setCurrentCPS(currentCPS);

					if (whitelistModule.meet(currentPPS, currentCPS, currentJPS)) {
						new Punish(plugin, moduleManager, locale, blacklistModule, connection, event);

						whitelistModule.setLastLockout(currentTimeMillis);
					} else if (blacklistModule.meetCheck(currentPPS, currentCPS, currentJPS, connection))
						new Punish(plugin, moduleManager, locale, blacklistModule, connection, event);
					else if (accountsModule.meetCheck(currentPPS, currentCPS, currentJPS, connection))
						new Punish(plugin, moduleManager, locale, accountsModule, connection, event);
					else if (reconnectModule.meetCheck(currentPPS, currentCPS, currentJPS, connection)) {
						botPlayer.setReconnects(botPlayer.getReconnects() + 1);

						new Punish(plugin, moduleManager, locale, reconnectModule, connection, event);
					} else if (rateLimitModule.meetCheck(connection)) {
						new Punish(plugin, moduleManager, locale, rateLimitModule, connection, event);
						blacklistModule.setBlacklisted(ip, true);
					} else if (nicknameModule.meetCheck(currentPPS, currentCPS, currentJPS, connection))
						new Punish(plugin, moduleManager, locale, nicknameModule, connection, event);
					else {
						nicknameModule.setLastNickname(name);

						if (botPlayer.getAccounts().isEmpty()) {
							playerModule.setOffline(botPlayer);
						}
					}

					botPlayer.setLastConnection(currentTimeMillis);
				} else {
					final String locale = "en", // Cant get locale on prelogin.
							ip = connection.getAddress().getHostString();
					final BotPlayer botPlayer = playerModule.get(ip);
					final long currentTimeMillis = System.currentTimeMillis();
					final int currentCPS = moduleManager.getCurrentCPS() + 1;

					botPlayer.setCPS(botPlayer.getCPS() + 1);
					moduleManager.setCurrentCPS(currentCPS);

					if (rateLimitModule.meetCheck(connection)) {
						new Punish(plugin, moduleManager, locale, rateLimitModule, connection, event);
						blacklistModule.setBlacklisted(ip, true);
					}

					botPlayer.setLastConnection(currentTimeMillis);
				}
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}
}
