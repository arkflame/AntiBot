package twolovers.antibot.bungee.listeners;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.SettingsChangedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import twolovers.antibot.bungee.instanceables.BotPlayer;
import twolovers.antibot.bungee.module.ModuleManager;
import twolovers.antibot.bungee.module.PlayerModule;
import twolovers.antibot.bungee.module.SettingsModule;

public class SettingsChangedListener implements Listener {
	private final PlayerModule playerModule;
	private final SettingsModule settingsModule;

	public SettingsChangedListener(final ModuleManager moduleManager) {
		this.playerModule = moduleManager.getPlayerModule();
		this.settingsModule = moduleManager.getSettingsModule();
	}

	@EventHandler(priority = Byte.MIN_VALUE)
	public void onSettingsChanged(final SettingsChangedEvent event) {
		final ProxiedPlayer proxiedPlayer = event.getPlayer();
		final String ip = proxiedPlayer.getAddress().getHostString();
		final BotPlayer botPlayer = playerModule.get(ip);

		botPlayer.setSettings(true);
		settingsModule.removePending(botPlayer);
	}
}
