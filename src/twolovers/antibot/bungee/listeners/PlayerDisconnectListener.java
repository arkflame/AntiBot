package twolovers.antibot.bungee.listeners;

import java.util.Locale;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import twolovers.antibot.bungee.instanceables.BotPlayer;
import twolovers.antibot.bungee.module.ModuleManager;
import twolovers.antibot.bungee.module.NotificationsModule;
import twolovers.antibot.bungee.module.PlayerModule;
import twolovers.antibot.bungee.module.SettingsModule;
import twolovers.antibot.bungee.module.WhitelistModule;

public class PlayerDisconnectListener implements Listener {
	private final ModuleManager moduleManager;

	public PlayerDisconnectListener(final ModuleManager moduleManager) {
		this.moduleManager = moduleManager;
	}

	@EventHandler
	public void onPlayerDisconnect(final PlayerDisconnectEvent event) {
		final NotificationsModule notificationsModule = moduleManager.getNotificationsModule();
		final PlayerModule playerModule = moduleManager.getPlayerModule();
		final SettingsModule settingsModule = moduleManager.getSettingsModule();
		final WhitelistModule whitelistModule = moduleManager.getWhitelistModule();
		final ProxiedPlayer proxiedPlayer = event.getPlayer();
		final String name = proxiedPlayer.getName(), ip = proxiedPlayer.getAddress().getHostString();
		final BotPlayer botPlayer = playerModule.get(ip);
		final long currentTime = System.currentTimeMillis();

		if ((proxiedPlayer.getLocale() != Locale.US || proxiedPlayer.getPing() < 500)
				&& (!whitelistModule.isRequireSwitch() || botPlayer.getSwitchs() > 1)
				&& currentTime - botPlayer.getLastConnection() >= whitelistModule.getTimeWhitelist()) {
			whitelistModule.setWhitelisted(ip, true);
		}

		botPlayer.removePlayer(proxiedPlayer);
		botPlayer.clearSwitchs();
		botPlayer.setLastConnection(currentTime);
		notificationsModule.setNotifications(proxiedPlayer, false);
		settingsModule.removePending(botPlayer);

		if (name.equals(botPlayer.getLastNickname())) {
			botPlayer.setSettings(false);
		}

		if (botPlayer.getPlayers().size() < 1) {
			playerModule.setOffline(botPlayer);
		}
	}
}
