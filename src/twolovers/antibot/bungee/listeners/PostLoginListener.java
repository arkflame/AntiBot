package twolovers.antibot.bungee.listeners;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import twolovers.antibot.bungee.instanceables.BotPlayer;
import twolovers.antibot.bungee.module.ModuleManager;
import twolovers.antibot.bungee.module.NotificationsModule;
import twolovers.antibot.bungee.module.PlayerModule;
import twolovers.antibot.bungee.module.SettingsModule;

public class PostLoginListener implements Listener {
	private final ModuleManager moduleManager;

	public PostLoginListener(final Plugin plugin, final ModuleManager moduleManager) {
		this.moduleManager = moduleManager;
		this.moduleManager.getSettingsModule();
		moduleManager.getRateLimitModule();
		this.moduleManager.getBlacklistModule();
	}

	@EventHandler(priority = -128)
	public void onPostLogin(final PostLoginEvent event) {
		final NotificationsModule notificationsModule = moduleManager.getNotificationsModule();
		final PlayerModule playerModule = moduleManager.getPlayerModule();
		final SettingsModule settingsModule = moduleManager.getSettingsModule();
		final ProxiedPlayer proxiedPlayer = event.getPlayer();
		final String ip = proxiedPlayer.getAddress().getHostString();
		final BotPlayer botPlayer = playerModule.get(ip);

		botPlayer.setJPS(botPlayer.getJPS() + 1);
		botPlayer.addPlayer(proxiedPlayer);
		moduleManager.setCurrentJPS(moduleManager.getCurrentJPS() + 1);
		settingsModule.addPending(botPlayer);
		playerModule.setOnline(botPlayer);

		if (notificationsModule.isEnabled() && proxiedPlayer.hasPermission("antibot.notifications"))
			notificationsModule.setNotifications(proxiedPlayer, true);
	}
}