package twolovers.antibot.bungee.module;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import twolovers.antibot.bungee.utils.ConfigUtil;
import twolovers.antibot.shared.interfaces.IModule;

public class NotificationsModule implements IModule {
	private final ModuleManager moduleManager;
	private final Logger logger;
	private static final String NAME = "notifications";
	private final Collection<ProxiedPlayer> notificationPlayers = new HashSet<>();
	private boolean enabled = true, console = true;
	private long lastNotificationTime = System.currentTimeMillis();

	NotificationsModule(final ModuleManager moduleManager, final Logger logger) {
		this.moduleManager = moduleManager;
		this.logger = logger;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void reload(final ConfigUtil configUtil) {
		final Configuration configYml = configUtil.getConfiguration("%datafolder%/config.yml");

		enabled = configYml.getBoolean(NAME + ".enabled", enabled);
		console = configYml.getBoolean(NAME + ".console", console);
	}

	public void notify(final String locale, final String address, final String checkName) {
		if (enabled) {
			try {
				final PlaceholderModule placeholderModule = moduleManager.getPlaceholderModule();
				final String notification = placeholderModule.setPlaceholders(moduleManager, "%notification_message%",
						locale, address, checkName);
				final BaseComponent[] notificationTextComponent = TextComponent.fromLegacyText(notification);
				final ChatMessageType chatMessageType = ChatMessageType.ACTION_BAR;
				final long currentTime = System.currentTimeMillis();

				if (console) {
					logger.log(Level.INFO, notification);
				}

				if (currentTime > lastNotificationTime + 100) {
					for (final ProxiedPlayer player : notificationPlayers) {
						player.sendMessage(chatMessageType, notificationTextComponent);
					}

					lastNotificationTime = currentTime;
				}
			} catch (final ConcurrentModificationException e) {
				logger.warning("AntiBot catched a CME exception! (NotificationsModule.java)");
			}
		}
	}

	public void setNotifications(final ProxiedPlayer player, final boolean bool) {
		if (bool) {
			if (!notificationPlayers.contains(player)) {
				notificationPlayers.add(player);
			}
		} else if (notificationPlayers.contains(player)) {
			notificationPlayers.remove(player);
		}
	}

	public boolean hasNotifications(final ProxiedPlayer player) {
		return notificationPlayers.contains(player);
	}

	public boolean isEnabled() {
		return enabled;
	}
}