package twolovers.antibot.bungee.module;

import java.util.Collection;
import java.util.HashSet;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import twolovers.antibot.bungee.utils.ConfigUtil;
import twolovers.antibot.shared.interfaces.Module;

public class NotificationsModule implements Module {
	private final String name = "notifications";
	private final Collection<ProxiedPlayer> notifications = new HashSet<>();
	private boolean enabled, console;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void reload(final ConfigUtil configUtil) {
		final Configuration configYml = configUtil.getConfiguration("%datafolder%/config.yml");

		this.enabled = configYml.getBoolean(name + ".enabled", true);
		this.console = configYml.getBoolean(name + ".console", true);
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public boolean isConsole() {
		return this.console;
	}

	public void setNotifications(final ProxiedPlayer player, final boolean bool) {
		if (bool) {
			if (!notifications.contains(player))
				notifications.add(player);
		} else
			notifications.remove(player);
	}

	public boolean hasNotifications(final ProxiedPlayer player) {
		return notifications.contains(player);
	}

	public Iterable<ProxiedPlayer> getNotificationPlayers() {
		return notifications;
	}
}