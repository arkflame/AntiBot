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
import twolovers.antibot.bungee.utils.BungeeUtil;

public class ChatListener implements Listener {
	private final Plugin plugin;
	private final ModuleManager moduleManager;

	public ChatListener(final Plugin plugin, final ModuleManager moduleManager) {
		this.plugin = plugin;
		this.moduleManager = moduleManager;
	}

	@EventHandler(priority = Byte.MIN_VALUE)
	public void onChat(final ChatEvent event) {
		final Connection sender = event.getSender();

		if (!event.isCancelled() && sender instanceof ProxiedPlayer) {
			final WhitelistModule whitelistModule = moduleManager.getWhitelistModule();
			final ProxiedPlayer proxiedPlayer = (ProxiedPlayer) sender;

			if (!whitelistModule.check(proxiedPlayer)) {
				final RegisterModule registerModule = moduleManager.getRegisterModule();
				final FastChatModule fastChatModule = moduleManager.getFastChatModule();
				final String defaultLanguage = moduleManager.getDefaultLanguage();
				final String message = event.getMessage().trim();
				final Locale locale = proxiedPlayer.getLocale();
				final int currentPps = moduleManager.getCurrentPps();
				final int currentCps = moduleManager.getCurrentCps();
				final int currentJps = moduleManager.getCurrentJps();
				final int lastPps = moduleManager.getLastPps();
				final int lastCps = moduleManager.getLastCps();
				final int lastJps = moduleManager.getLastJps();

				if (locale == null) {
					if (fastChatModule.meet(currentPps, currentCps, currentJps, lastPps, lastCps, lastJps)
							&& fastChatModule.check(proxiedPlayer)) {
						new Punish(plugin, moduleManager, defaultLanguage, fastChatModule, proxiedPlayer, event);

						moduleManager.getBlacklistModule().setBlacklisted(proxiedPlayer.getAddress().getHostString(),
								true);
					}
				} else {
					final String lang = BungeeUtil.getLanguage(proxiedPlayer, defaultLanguage);

					if (fastChatModule.meet(currentPps, currentCps, currentJps, lastPps, lastCps, lastJps)
							&& fastChatModule.check(proxiedPlayer)) {
						new Punish(plugin, moduleManager, lang, fastChatModule, proxiedPlayer, event);
					} else if (registerModule.meet(currentPps, currentCps, currentJps, lastPps, lastCps, lastJps)
							&& registerModule.check(proxiedPlayer, message)) {
						new Punish(plugin, moduleManager, lang, registerModule, proxiedPlayer, event);
					} else {
						registerModule.setLastValues(proxiedPlayer.getAddress().getHostString(), message);
					}
				}
			}
		}
	}
}