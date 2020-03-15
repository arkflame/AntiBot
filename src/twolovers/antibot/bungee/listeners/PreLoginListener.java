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

	@EventHandler(priority = -128)
	public void onPreLogin(final PreLoginEvent event) {
		if (!event.isCancelled()) {
			final AccountsModule accountsModule = moduleManager.getAccountsModule();
			final BlacklistModule blacklistModule = moduleManager.getBlacklistModule();
			final NicknameModule nicknameModule = moduleManager.getNicknameModule();
			final PlayerModule playerModule = moduleManager.getPlayerModule();
			final RateLimitModule rateLimitModule = moduleManager.getRateLimitModule();
			final ReconnectModule reconnectModule = moduleManager.getReconnectModule();
			final WhitelistModule whitelistModule = moduleManager.getWhitelistModule();
			final PendingConnection connection = event.getConnection();
			final String name = connection.getName();
			final String locale = "en", // Cant get locale on prelogin.
					ip = connection.getAddress().getHostString();
			final BotPlayer botPlayer = playerModule.get(ip);
			final long currentTimeMillis = System.currentTimeMillis();
			final int currentPPS = moduleManager.getCurrentPPS();
			final int currentCPS = moduleManager.getCurrentCPS() + 1;
			final int currentJPS = moduleManager.getCurrentJPS();

			botPlayer.setCPS(botPlayer.getCPS() + 1);
			moduleManager.setCurrentCPS(currentCPS);

			if (whitelistModule.meet(currentPPS, currentCPS, currentJPS)) {
				new Punish(plugin, moduleManager, locale, blacklistModule.getPunishCommands(), connection, event,
						"Whitelist");

				whitelistModule.setLastLockout(currentTimeMillis);
			} else if (blacklistModule.meet(currentPPS, currentCPS, currentJPS) && blacklistModule.check(connection))
				new Punish(plugin, moduleManager, locale, blacklistModule.getPunishCommands(), connection, event,
						"Blacklist");
			else if (accountsModule.meet(currentPPS, currentCPS, currentJPS) && accountsModule.check(connection))
				new Punish(plugin, moduleManager, locale, accountsModule.getPunishCommands(), connection, event,
						"Accounts");
			else if (reconnectModule.meet(currentPPS, currentCPS, currentJPS) && reconnectModule.check(connection)) {
				new Punish(plugin, moduleManager, locale, reconnectModule.getPunishCommands(), connection, event,
						"Reconnect");
			} else if (rateLimitModule.meet(currentPPS, currentCPS, currentJPS) && rateLimitModule.check(connection)) {
				new Punish(plugin, moduleManager, locale, rateLimitModule.getPunishCommands(), connection, event,
						"Ratelimit");
				blacklistModule.setBlacklisted(ip, true);
			} else if (nicknameModule.meet(currentPPS, currentCPS, currentJPS) && nicknameModule.check(connection))
				new Punish(plugin, moduleManager, locale, nicknameModule.getPunishCommands(), connection, event,
						"Nickname");
			else {
				nicknameModule.setLastNickname(name);

				if (botPlayer.getPlayers().size() < 1) {
					playerModule.setOffline(botPlayer);
				}
			}

			botPlayer.setSettings(false);
			botPlayer.setLastConnection(currentTimeMillis);
		}
	}
}
