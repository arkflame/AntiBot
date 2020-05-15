package twolovers.antibot.bungee.instanceables;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.plugin.Plugin;
import twolovers.antibot.bungee.module.ModuleManager;
import twolovers.antibot.bungee.module.NotificationsModule;
import twolovers.antibot.bungee.module.PlaceholderModule;
import twolovers.antibot.shared.interfaces.PunishModule;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

public class Punish {
	public Punish(final Plugin plugin, final ModuleManager moduleManager, final String locale,
			final PunishModule punishModule, final Connection connection, final Event event) {
		final PlaceholderModule placeholderModule = moduleManager.getPlaceholderModule();
		final NotificationsModule notificationsModule = moduleManager.getNotificationsModule();
		final Collection<String> punishCommands = punishModule.getPunishCommands();
		final String punishModuleName = punishModule.getName(),
				checkName = punishModuleName.substring(0, 1).toUpperCase() + punishModuleName.substring(1),
				address = connection.getAddress().getHostString();

		if (notificationsModule.isEnabled()) {
			final String notification = placeholderModule.replacePlaceholders(locale, "%notification_message%", address,
					checkName, new AtomicInteger(0));
			final TextComponent notificationTextComponent = new TextComponent(notification);

			if (notificationsModule.isConsole()) {
				plugin.getLogger().log(Level.INFO, notification);
			}

			for (final ProxiedPlayer proxiedPlayer : notificationsModule.getNotificationPlayers()) {
				proxiedPlayer.sendMessage(ChatMessageType.ACTION_BAR, notificationTextComponent);
			}
		}

		if (event instanceof ProxyPingEvent) {
			final ProxyPingEvent proxyPingEvent = (ProxyPingEvent) event;

			proxyPingEvent.setResponse(null);
		} else if (!punishCommands.isEmpty()) {
			for (String command : punishCommands) {
				command = placeholderModule.replacePlaceholders(locale, command, address, checkName,
						new AtomicInteger(0));

				if (command.startsWith("disconnect")) {
					final BaseComponent[] disconnectMessage = TextComponent
							.fromLegacyText(command.replace("disconnect ", ""));

					if (event instanceof PreLoginEvent) {
						final PreLoginEvent preLoginEvent = (PreLoginEvent) event;

						preLoginEvent.setCancelReason(disconnectMessage);
						preLoginEvent.setCancelled(true);
					} else if (event instanceof Cancellable) {
						((Cancellable) event).setCancelled(true);
					}

					connection.disconnect(disconnectMessage);
				} else {
					final ProxyServer proxyServer = plugin.getProxy();

					proxyServer.getPluginManager().dispatchCommand(proxyServer.getConsole(), command);
				}
			}
		}
	}
}
