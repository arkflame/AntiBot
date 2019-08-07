package twolovers.antibot.bungee.listeners;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import twolovers.antibot.bungee.managers.ModuleManager;
import twolovers.antibot.bungee.modules.BlacklistModule;
import twolovers.antibot.bungee.modules.NotificationsModule;
import twolovers.antibot.bungee.modules.SettingsModule;
import twolovers.antibot.bungee.modules.WhitelistModule;

import java.util.Locale;

public class ServerSwitchListener implements Listener {
	private final NotificationsModule notificationsModule;
	private final SettingsModule settingsModule;
	private final BlacklistModule blacklistModule;
	private final WhitelistModule whitelistModule;

	public ServerSwitchListener(ModuleManager moduleManager) {
		this.notificationsModule = moduleManager.getNotificationsModule();
		this.settingsModule = moduleManager.getSettingsModule();
		this.blacklistModule = moduleManager.getBlacklistModule();
		this.whitelistModule = moduleManager.getWhitelistModule();
	}

	@EventHandler(priority = -128)
	public void onServerSwitch(final ServerSwitchEvent event) {
		final ProxiedPlayer proxiedPlayer = event.getPlayer();
		final String ip = proxiedPlayer.getAddress().getAddress().getHostAddress();

		if (settingsModule.isSwitched(ip) && settingsModule.isCondition() && proxiedPlayer.getLocale() == null) {
			notificationsModule.sendNotification("Settings", "CPS", ip);

			if (proxiedPlayer.isConnected())
				proxiedPlayer.disconnect(new TextComponent(settingsModule.getSettingsKickMessage()));
		} else if (!settingsModule.isSwitched(ip))
			settingsModule.setSwitched(ip, true);
		else if (proxiedPlayer.getLocale() != Locale.US || proxiedPlayer.getPing() < 700)
			whitelistModule.setWhitelisted(ip, true);
	}
}
