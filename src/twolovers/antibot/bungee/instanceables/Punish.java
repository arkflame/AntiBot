package twolovers.antibot.bungee.instanceables;

import java.util.Collection;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;
import twolovers.antibot.bungee.module.ModuleManager;
import twolovers.antibot.bungee.module.NotificationsModule;
import twolovers.antibot.bungee.module.PlaceholderModule;
import twolovers.antibot.shared.interfaces.IPunishModule;

public class Punish {
	public Punish(final ModuleManager moduleManager, final String locale,
			final IPunishModule punishModule, final Connection connection, final Event event) {
		final PlaceholderModule placeholderModule = moduleManager.getPlaceholderModule();
		final NotificationsModule notificationsModule = moduleManager.getNotificationsModule();
		final Collection<String> punishCommands = punishModule.getPunishCommands();
		final String punishModuleName = punishModule.getName();
		final String checkName = punishModuleName.substring(0, 1).toUpperCase() + punishModuleName.substring(1);
		final String address = connection.getAddress().getHostString();

		moduleManager.getCounterModule().addTotalBlocked();
		notificationsModule.notify(locale, address, checkName);

		if (event instanceof ProxyPingEvent) {
			final ProxyPingEvent proxyPingEvent = (ProxyPingEvent) event;

			proxyPingEvent.setResponse(null);
		} else if (!punishCommands.isEmpty()) {
			final String disconnectString = "disconnect ";

			for (String command : punishCommands) {
				command = placeholderModule.setPlaceholders(moduleManager, command, locale, address, checkName);

				if (command.startsWith(disconnectString)) {
					final BaseComponent[] textComponent = TextComponent
							.fromLegacyText(command.replace(disconnectString, ""));

					if (event instanceof Cancellable) {
						if (event instanceof PreLoginEvent) {
							final PreLoginEvent preLoginEvent = (PreLoginEvent) event;

							preLoginEvent.setCancelReason(textComponent);
						} else {
							connection.disconnect(textComponent);
						}

						((Cancellable) event).setCancelled(true);
					} else {
						connection.disconnect(textComponent);
					}
				} else {
					final ProxyServer proxyServer = BungeeCord.getInstance();

					proxyServer.getPluginManager().dispatchCommand(proxyServer.getConsole(), command);
				}
			}
		}
	}
}
