package twolovers.antibot.bungee.listeners;

import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import twolovers.antibot.bungee.modules.NotificationsModule;
import twolovers.antibot.bungee.modules.SettingsModule;
import twolovers.antibot.bungee.managers.ModuleManager;

public class PlayerDisconnectListener implements Listener {
	private final ModuleManager moduleManager;
	private final SettingsModule settingsModule;
	private final NotificationsModule notificationsModule;

	public PlayerDisconnectListener(final ModuleManager moduleManager) {
		this.moduleManager = moduleManager;
		this.settingsModule = moduleManager.getSettingsModule();
		this.notificationsModule = moduleManager.getNotificationsModule();
	}

	@EventHandler
	public void onPlayerDisconnect(final PlayerDisconnectEvent event) {
		final String ip = event.getPlayer().getAddress().getAddress().getHostAddress();

		moduleManager.getRateLimitModule().removeOnline(ip, 1);
		settingsModule.setSwitched(ip, false);
		notificationsModule.setNotifications(event.getPlayer(), false);
	}
}
