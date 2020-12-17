package twolovers.antibot.bungee.listeners;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import twolovers.antibot.bungee.instanceables.BotPlayer;
import twolovers.antibot.bungee.instanceables.Punish;
import twolovers.antibot.bungee.module.ModuleManager;
import twolovers.antibot.bungee.module.PlayerModule;
import twolovers.antibot.bungee.module.SettingsModule;

public class ServerSwitchListener implements Listener {
	private final Plugin plugin;
	private final ModuleManager moduleManager;
	private final SettingsModule settingsModule;

	public ServerSwitchListener(final Plugin plugin, final ModuleManager moduleManager) {
		this.plugin = plugin;
		this.moduleManager = moduleManager;
		this.settingsModule = moduleManager.getSettingsModule();
		moduleManager.getBlacklistModule();
		moduleManager.getWhitelistModule();
	}

	@EventHandler(priority = Byte.MIN_VALUE)
	public void onServerSwitch(final ServerSwitchEvent event) {
		final PlayerModule playerModule = moduleManager.getPlayerModule();
		final ProxiedPlayer proxiedPlayer = event.getPlayer();
		final String ip = proxiedPlayer.getAddress().getHostString();
		final BotPlayer botPlayer = playerModule.get(ip);

		if (settingsModule.isSwitching()) {
			final int currentPps = moduleManager.getCurrentPps();
			final int currentCps = moduleManager.getCurrentCps();
			final int currentJps = moduleManager.getCurrentJps();
			final int lastPps = moduleManager.getLastPps();
			final int lastCps = moduleManager.getLastCps();
			final int lastJps = moduleManager.getLastJps();
			final boolean switched = botPlayer.getSwitchs() > 0;

			if (switched && settingsModule.meet(currentPps, currentCps, currentJps, lastPps, lastCps, lastJps)
					&& !botPlayer.isSettings()) {
				new Punish(plugin, moduleManager, "en", settingsModule, proxiedPlayer, event);
			}
		}

		botPlayer.addSwitch();
	}
}
