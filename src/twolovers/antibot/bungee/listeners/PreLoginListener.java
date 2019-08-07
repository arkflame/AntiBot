package twolovers.antibot.bungee.listeners;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import twolovers.antibot.bungee.managers.ModuleManager;
import twolovers.antibot.bungee.modules.*;

public class PreLoginListener implements Listener {
	private final ModuleManager moduleManager;
	private final AccountsModule accountsModule;
	private final RateLimitModule rateLimitModule;
	private final NotificationsModule notificationsModule;
	private final BlacklistModule blacklistModule;
	private final WhitelistModule whitelistModule;
	private final ReconnectModule reconnectModule;
	private final NicknameModule nicknameModule;

	public PreLoginListener(ModuleManager moduleManager) {
		this.moduleManager = moduleManager;
		this.accountsModule = moduleManager.getAccountsModule();
		this.rateLimitModule = moduleManager.getRateLimitModule();
		this.notificationsModule = moduleManager.getNotificationsModule();
		this.blacklistModule = moduleManager.getBlacklistModule();
		this.whitelistModule = moduleManager.getWhitelistModule();
		this.reconnectModule = moduleManager.getReconnectModule();
		this.nicknameModule = moduleManager.getNicknameModule();
	}

	@EventHandler(priority = -128)
	public void onPreLogin(final PreLoginEvent event) {
		if (!event.isCancelled()) {
			final Connection connection = event.getConnection();
			final String ip = event.getConnection().getAddress().getAddress().getHostAddress();
			final String name = event.getConnection().getName();
			final long currentTime = System.currentTimeMillis();
			final long lastPing = moduleManager.getLastPing(ip);
			final long lastConnection = moduleManager.getLastConnection(ip);
			final boolean hasThrottle = currentTime - lastConnection < rateLimitModule.getRateLimitThrottle();

			moduleManager.addCPS(ip, 1);

			if (hasThrottle) {
				event.setCancelReason(new TextComponent(rateLimitModule.getKickMessage()));
				event.setCancelled(true);

				notificationsModule.sendNotification("Ratelimit", "CPS", ip);
			} else if (rateLimitModule.isCondition(ip)) {
				event.setCancelReason(new TextComponent(rateLimitModule.getKickMessage()));
				event.setCancelled(true);

				blacklistModule.setBlacklisted(ip, true);
				notificationsModule.sendNotification("Ratelimit", "CPS", ip);
			} else if (!whitelistModule.isEnabled() || !whitelistModule.isWhitelisted(ip)) {
				if (whitelistModule.isCondition()) {
					event.setCancelReason(new TextComponent(whitelistModule.getKickMessage()));
					event.setCancelled(true);

					notificationsModule.sendNotification("Whitelist", "CPS", ip);
				} else if (blacklistModule.isCondition(ip)) {
					event.setCancelReason(new TextComponent(blacklistModule.getKickMessage()));
					event.setCancelled(true);

					notificationsModule.sendNotification("Blacklist", "CPS", ip);
				} else if (accountsModule.isCondition() && accountsModule.getAccountCount(ip) > accountsModule.getLimit()) {
					event.setCancelReason(new TextComponent(accountsModule.getKickMessage()));
					event.setCancelled(true);

					blacklistModule.setBlacklisted(ip, true);
					notificationsModule.sendNotification("Accounts", "CPS", ip);
				} else if (reconnectModule.isCondition() && reconnectModule.getReconnects(ip) < reconnectModule.getReconnectTimes()) {
					if (reconnectModule.isForceRejoinPingEnabled()) {
						if (lastPing > lastConnection && currentTime - lastConnection > reconnectModule.getReconnectTime())
							reconnectModule.addReconnect(ip, 1);
					} else if (currentTime - lastConnection > reconnectModule.getReconnectTime())
						reconnectModule.addReconnect(ip, 1);

					event.setCancelReason(new TextComponent(reconnectModule.getKickMessage(ip)));
					event.setCancelled(true);

					notificationsModule.sendNotification("Reconnect", "CPS", ip);
				} else if (nicknameModule.isCondition()) {
					if (name.length() == moduleManager.getLastNickname().length()) {
						event.setCancelReason(new TextComponent(nicknameModule.getKickMessage()));
						event.setCancelled(true);

						blacklistModule.setBlacklisted(ip, true);
						notificationsModule.sendNotification("Nickname", "CPS", ip);
					} else if (nicknameModule.cotainsString(name.toLowerCase())) {
						event.setCancelReason(new TextComponent(nicknameModule.getKickMessage()));
						event.setCancelled(true);

						blacklistModule.setBlacklisted(ip, true);
						notificationsModule.sendNotification("Nickname", "CPS", ip);
					} else if (name.matches(nicknameModule.getPattern())) {
						event.setCancelReason(new TextComponent(nicknameModule.getKickMessage()));
						event.setCancelled(true);

						blacklistModule.setBlacklisted(ip, true);
						notificationsModule.sendNotification("Nickname", "CPS", ip);
					}
				} else {
					moduleManager.setLastNickname(name);
					reconnectModule.addReconnect(ip, 0);
				}

				moduleManager.setLastConnection(ip, currentTime);
				accountsModule.addAccount(ip, name);
			}

			moduleManager.setLastConnection(ip, currentTime);
		}
	}
}
