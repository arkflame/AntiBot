package twolovers.antibot.bungee.listeners;

import java.util.Locale;

import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import twolovers.antibot.bungee.instanceables.Punish;
import twolovers.antibot.bungee.module.FastChatModule;
import twolovers.antibot.bungee.module.ModuleManager;
import twolovers.antibot.bungee.module.PasswordModule;
import twolovers.antibot.bungee.module.WhitelistModule;
import twolovers.antibot.bungee.utils.BungeeUtil;
import twolovers.antibot.bungee.utils.Incoming;

public class ChatListener implements Listener {
	private final ModuleManager moduleManager;

	public ChatListener(final ModuleManager moduleManager) {
		this.moduleManager = moduleManager;
	}

	@EventHandler(priority = Byte.MIN_VALUE)
	public void onChat(final ChatEvent event) {
		final Connection sender = event.getSender();

		if (!event.isCancelled() && sender instanceof ProxiedPlayer) {
			final WhitelistModule whitelistModule = moduleManager.getWhitelistModule();
			final ProxiedPlayer proxiedPlayer = (ProxiedPlayer) sender;

			if (!whitelistModule.check(proxiedPlayer)) {
				final PasswordModule registerModule = moduleManager.getRegisterModule();
				final FastChatModule fastChatModule = moduleManager.getFastChatModule();
				final String defaultLanguage = moduleManager.getDefaultLanguage();
				final String message = event.getMessage().trim();
				final Locale locale = proxiedPlayer.getLocale();
				final Incoming currentIncoming = moduleManager.getCounterModule().getCurrent();
				final Incoming lastIncoming = moduleManager.getCounterModule().getLast();

				if (locale == null) {
					if (fastChatModule.meet(currentIncoming, lastIncoming)
							&& fastChatModule.check(proxiedPlayer)) {
						new Punish(moduleManager, defaultLanguage, fastChatModule, proxiedPlayer, event);

						moduleManager.getBlacklistModule().setBlacklisted(proxiedPlayer.getAddress().getHostString(),
								true);
					}
				} else {
					final String lang = BungeeUtil.getLanguage(proxiedPlayer, defaultLanguage);

					if (fastChatModule.meet(currentIncoming, lastIncoming)
							&& fastChatModule.check(proxiedPlayer)) {
						new Punish(moduleManager, lang, fastChatModule, proxiedPlayer, event);
					} else if (registerModule.meet(currentIncoming, lastIncoming)
							&& registerModule.check(proxiedPlayer, message)) {
						new Punish(moduleManager, lang, registerModule, proxiedPlayer, event);
					} else {
						registerModule.setLastValues(proxiedPlayer.getAddress().getHostString(), message);
					}
				}
			}
		}
	}
}