package twolovers.antibot.bungee.module;

import java.util.Collection;
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
	private final String name = "notifications";
	private final Collection<ProxiedPlayer> notificationPlayers = new HashSet<>();
	private boolean enabled = true, console = true;

	NotificationsModule(final ModuleManager moduleManager, final Logger logger) {
		this.moduleManager = moduleManager;
		this.logger = logger;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void reload(final ConfigUtil configUtil) {
		final Configuration configYml = configUtil.getConfiguration("%datafolder%/config.yml");

		enabled = configYml.getBoolean(name + ".enabled", enabled);
		console = configYml.getBoolean(name + ".console", console);
	}

	public void notify(final String locale, final String address, final String checkName) {
		if (this.enabled) {
			final PlaceholderModule placeholderModule = moduleManager.getPlaceholderModule();
			final String notification = placeholderModule.setPlaceholders(moduleManager, "%notification_message%",
					locale, address, checkName);
			final BaseComponent[] notificationTextComponent = TextComponent.fromLegacyText(notification);

			if (this.console) {
				this.logger.log(Level.INFO, notification);
			}

			for (final ProxiedPlayer proxiedPlayer : this.notificationPlayers) {
				proxiedPlayer.sendMessage(ChatMessageType.ACTION_BAR, notificationTextComponent);
			}
		}
	}

	public void setNotifications(final ProxiedPlayer player, final boolean bool) {
		if (this.enabled) {
			if (bool) {
				if (!this.notificationPlayers.contains(player)) {
					this.notificationPlayers.add(player);
				}
			} else if (this.notificationPlayers.contains(player)) {
				this.notificationPlayers.remove(player);
			}
		}
	}

	public boolean hasNotifications(final ProxiedPlayer player) {
		return notificationPlayers.contains(player);
	}
}