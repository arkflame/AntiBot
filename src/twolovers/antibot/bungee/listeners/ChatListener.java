package twolovers.antibot.bungee.listeners;

import java.util.Locale;

import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import twolovers.antibot.bungee.instanceables.Punish;
import twolovers.antibot.bungee.module.FastChatModule;
import twolovers.antibot.bungee.module.ModuleManager;
import twolovers.antibot.bungee.module.RegisterModule;
import twolovers.antibot.bungee.module.WhitelistModule;

public class ChatListener implements Listener {
	private final Plugin plugin;
	private final ModuleManager moduleManager;

	public ChatListener(final Plugin plugin, final ModuleManager moduleManager) {
		this.plugin = plugin;
		this.moduleManager = moduleManager;
	}

	@EventHandler
	public void onChat(final ChatEvent event) {
		final Connection sender = event.getSender();

		if (!event.isCancelled() && sender instanceof ProxiedPlayer) {
			final WhitelistModule whitelistModule = moduleManager.getWhitelistModule();
			final ProxiedPlayer proxiedPlayer = (ProxiedPlayer) sender;

			if (!whitelistModule.check(proxiedPlayer)) {
				final RegisterModule registerModule = moduleManager.getRegisterModule();
				final FastChatModule fastChatModule = moduleManager.getFastChatModule();
				final String message = event.getMessage();
				final Locale locale = proxiedPlayer.getLocale();
				final int currentPPS = moduleManager.getCurrentPPS(), currentCPS = moduleManager.getCurrentCPS(),
						currentJPS = moduleManager.getCurrentJPS();

				if (locale == null) {
					if (fastChatModule.meet(currentPPS, currentCPS, currentJPS)) {
						new Punish(plugin, moduleManager, "en", fastChatModule, proxiedPlayer, event);

						moduleManager.getBlacklistModule().setBlacklisted(proxiedPlayer.getAddress().getHostString(),
								true);
					}
				} else {
					final String lang = locale.toLanguageTag();

					if (fastChatModule.meetCheck(currentPPS, currentCPS, currentJPS, proxiedPlayer)) {
						new Punish(plugin, moduleManager, lang, fastChatModule, proxiedPlayer, event);
					} else if (registerModule.meetCheck(currentPPS, currentCPS, currentJPS, proxiedPlayer, message)) {
						new Punish(plugin, moduleManager, lang, registerModule, proxiedPlayer, event);
					} else {
						registerModule.setLastValues(proxiedPlayer.getAddress().getHostString(), message);
					}
				}
			}
		}
	}
}