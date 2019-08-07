package twolovers.antibot.bungee.listeners;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import twolovers.antibot.bungee.managers.ModuleManager;
import twolovers.antibot.bungee.modules.*;

public class ChatListener implements Listener {
	private final BlacklistModule blacklistModule;
	private final FastChatModule fastChatModule;
	private final NotificationsModule notificationVariables;
	private final RegisterModule registerModule;
	private final WhitelistModule whitelistModule;

	public ChatListener(ModuleManager moduleManager) {
		this.blacklistModule = moduleManager.getBlacklistModule();
		this.fastChatModule = moduleManager.getFastChatModule();
		this.notificationVariables = moduleManager.getNotificationsModule();
		this.registerModule = moduleManager.getRegisterModule();
		this.whitelistModule = moduleManager.getWhitelistModule();
	}

	@EventHandler
	public void onChat(final ChatEvent event) {
		final Connection sender = event.getSender();

		if (!event.isCancelled() && sender instanceof ProxiedPlayer) {
			final ProxiedPlayer proxiedPlayer = (ProxiedPlayer) sender;
			final String ip = proxiedPlayer.getAddress().getAddress().getHostAddress();

			if (!whitelistModule.isWhitelisted(ip)) {
				final String message = event.getMessage();

				if (fastChatModule.isCondition(ip)) {
					event.setCancelled(true);

					if (proxiedPlayer.isConnected())
						proxiedPlayer.disconnect(new TextComponent(fastChatModule.getKickMessage()));

					blacklistModule.setBlacklisted(ip, true);
					notificationVariables.sendNotification("FastChat", "CPS", ip);
				} else if (message.startsWith("/reg") && message.split(" ").length > 1) {
					if (registerModule.isCondition(ip, message)) {
						event.setCancelled(true);

						if (proxiedPlayer.isConnected())
							proxiedPlayer.disconnect(new TextComponent(registerModule.getKickMessage()));

						blacklistModule.setBlacklisted(ip, true);
						notificationVariables.sendNotification("Register", "CPS", ip);
					} else
						registerModule.setLastRegisterCommand(ip, message);
				} // omg hay muchas cosas XD
			}
		}
	}
}