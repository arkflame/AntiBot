package twolovers.antibot.bungee.listeners;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import twolovers.antibot.bungee.instanceables.BotPlayer;
import twolovers.antibot.bungee.instanceables.Punish;
import twolovers.antibot.bungee.module.CounterModule;
import twolovers.antibot.bungee.module.ModuleManager;
import twolovers.antibot.bungee.module.PlayerModule;
import twolovers.antibot.bungee.module.SettingsModule;

public class ServerSwitchListener implements Listener {
	private final ModuleManager moduleManager;
	private final SettingsModule settingsModule;

	public ServerSwitchListener(final ModuleManager moduleManager) {
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
			final CounterModule counterModule = moduleManager.getCounterModule();
			final boolean switched = botPlayer.getSwitchs() > 0;

			if (switched && settingsModule.meet(counterModule.getCurrent(), counterModule.getLast())
					&& !botPlayer.isSettings()) {
				new Punish(moduleManager, moduleManager.getDefaultLanguage(), settingsModule, proxiedPlayer, event);
			}
		}

		botPlayer.addSwitch();
	}
}
