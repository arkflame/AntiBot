package twolovers.antibot.bungee.modules;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.command.ConsoleCommandSender;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.api.ChatColor;
import twolovers.antibot.bungee.managers.ModuleManager;
import twolovers.antibot.bungee.utils.ConfigurationUtil;

import java.util.HashSet;
import java.util.Set;

public class NotificationsModule {
	private final ConfigurationUtil configurationUtil;
	private final ModuleManager moduleManager;
	private final Set<ProxiedPlayer> notifications = new HashSet<>();
	private boolean enabled = true;
	private String message = "";

	public NotificationsModule(ConfigurationUtil configurationUtil, ModuleManager moduleManager) {
		this.configurationUtil = configurationUtil;
		this.moduleManager = moduleManager;
		reload();
	}

	public final void reload() {
		final Configuration config = this.configurationUtil.getConfiguration("%datafolder%/config.yml");

		if (config != null) {
			enabled = config.getBoolean("notifications.enabled");
			message = ChatColor.translateAlternateColorCodes('&', config.getString("notifications.message"));
		}
	}

	public final void sendNotification(final String check, final String type, final String ip) {
		if (enabled) {
			final ConsoleCommandSender consoleCommandSender = ConsoleCommandSender.getInstance();
			final String notification = getNotificationMessage(check, type, ip);

			consoleCommandSender.sendMessage(notification);

			for (final ProxiedPlayer proxiedPlayer : getNotifications())
				proxiedPlayer.sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(notification));
		}
	}

	private String getNotificationMessage(String check, String type, String ip) {
		int typeValue = 0;

		switch (type) {
			case "PPS":
				typeValue = moduleManager.getTotalPPS();
				break;
			case "CPS":
				typeValue = moduleManager.getTotalCPS();
				break;
		}

		return message
				.replace("%ip%", ip)
				.replace("%check%", check)
				.replace("%total%", String.valueOf(typeValue))
				.replace("%type%", type);
	}

	public final void setNotifications(final ProxiedPlayer proxiedPlayer, final boolean input) {
		if (input)
			notifications.add(proxiedPlayer);
		else
			notifications.remove(proxiedPlayer);
	}

	public final boolean isNotifications(final ProxiedPlayer proxiedPlayer) {
		return notifications.contains(proxiedPlayer);
	}

	public final Set<ProxiedPlayer> getNotifications() {
		return notifications;
	}
}
