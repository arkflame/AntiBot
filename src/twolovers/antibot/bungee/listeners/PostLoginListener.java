package twolovers.antibot.bungee.listeners;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import twolovers.antibot.bungee.managers.ModuleManager;
import twolovers.antibot.bungee.modules.BlacklistModule;
import twolovers.antibot.bungee.modules.NotificationsModule;
import twolovers.antibot.bungee.modules.RateLimitModule;
import twolovers.antibot.bungee.modules.SettingsModule;

public class PostLoginListener implements Listener {
	private final ModuleManager moduleManager;
	private final SettingsModule settingsModule;
	private final RateLimitModule rateLimitModule;
	private final NotificationsModule notificationsModule;
	private final BlacklistModule blacklistModule;

	public PostLoginListener(final ModuleManager moduleManager) {
		this.moduleManager = moduleManager;
		this.settingsModule = moduleManager.getSettingsModule();
		this.rateLimitModule = moduleManager.getRateLimitModule();
		this.notificationsModule = moduleManager.getNotificationsModule();
		this.blacklistModule = moduleManager.getBlacklistModule();
	}

	@EventHandler(priority = -128)
	public void onPostLogin(final PostLoginEvent event) {
		final String ip = event.getPlayer().getAddress().getAddress().getHostAddress();
		final ProxiedPlayer proxiedPlayer = event.getPlayer();

		moduleManager.addJPS(ip, 1);
		settingsModule.setSwitched(ip, false);
		rateLimitModule.addOnline(ip, 1);

		if (rateLimitModule.isCondition(ip)) {
			if (proxiedPlayer.isConnected())
				proxiedPlayer.disconnect(new TextComponent(rateLimitModule.getKickMessage()));

			blacklistModule.setBlacklisted(ip, true);
			notificationsModule.sendNotification("RateLimit", "CPS", ip);
		} else if (rateLimitModule.getOnline(ip) >= rateLimitModule.getMaxOnline()) {
			if (proxiedPlayer.isConnected())
				proxiedPlayer.disconnect(new TextComponent(rateLimitModule.getKickMessage()));

			notificationsModule.sendNotification("RateLimit", "CPS", ip);
		} else if (proxiedPlayer.hasPermission("antibot.notifications"))
			notificationsModule.setNotifications(proxiedPlayer, true);
	}
}