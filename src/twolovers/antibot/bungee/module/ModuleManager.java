package twolovers.antibot.bungee.module;

import java.util.Iterator;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import twolovers.antibot.bungee.instanceables.BotPlayer;
import twolovers.antibot.bungee.instanceables.Punish;
import twolovers.antibot.bungee.utils.BungeeUtil;
import twolovers.antibot.bungee.utils.ConfigUtil;

public class ModuleManager {
	private final Plugin plugin;
	private final ConfigUtil configUtil;
	private final AccountsModule accountsModule;
	private final BlacklistModule blacklistModule;
	private final FastChatModule fastChatModule;
	private final ReconnectModule reconnectModule;
	private final NicknameModule nicknameModule;
	private final NotificationsModule notificationsModule;
	private final PlaceholderModule placeholderModule;
	private final PlayerModule playerModule;
	private final RateLimitModule rateLimitModule;
	private final RegisterModule registerModule;
	private final SettingsModule settingsModule;
	private final WhitelistModule whitelistModule;
	private int lastPPS = 0, lastCPS = 0, lastJPS = 0, currentPPS = 0, currentCPS = 0, currentJPS = 0;

	public ModuleManager(final Plugin plugin, final ConfigUtil configUtil) {
		this.plugin = plugin;
		this.configUtil = configUtil;
		this.placeholderModule = new PlaceholderModule(plugin, this);
		this.playerModule = new PlayerModule();
		this.accountsModule = new AccountsModule(this);
		this.blacklistModule = new BlacklistModule(this);
		this.fastChatModule = new FastChatModule(this);
		this.reconnectModule = new ReconnectModule(this);
		this.nicknameModule = new NicknameModule(this);
		this.notificationsModule = new NotificationsModule();
		this.rateLimitModule = new RateLimitModule(this);
		this.registerModule = new RegisterModule(this);
		this.settingsModule = new SettingsModule(this);
		this.whitelistModule = new WhitelistModule(this);
	}

	public final void reload() {
		this.placeholderModule.reload(configUtil);
		this.playerModule.reload(configUtil);
		this.accountsModule.reload(configUtil);
		this.blacklistModule.reload(configUtil);
		this.fastChatModule.reload(configUtil);
		this.reconnectModule.reload(configUtil);
		this.nicknameModule.reload(configUtil);
		this.notificationsModule.reload(configUtil);
		this.rateLimitModule.reload(configUtil);
		this.registerModule.reload(configUtil);
		this.settingsModule.reload(configUtil);
		this.whitelistModule.reload(configUtil);
	}

	public final AccountsModule getAccountsModule() {
		return accountsModule;
	}

	public final BlacklistModule getBlacklistModule() {
		return blacklistModule;
	}

	public final FastChatModule getFastChatModule() {
		return fastChatModule;
	}

	public final NicknameModule getNicknameModule() {
		return nicknameModule;
	}

	public final NotificationsModule getNotificationsModule() {
		return notificationsModule;
	}

	public PlaceholderModule getPlaceholderModule() {
		return placeholderModule;
	}

	public PlayerModule getPlayerModule() {
		return playerModule;
	}

	public final RateLimitModule getRateLimitModule() {
		return rateLimitModule;
	}

	public final ReconnectModule getReconnectModule() {
		return reconnectModule;
	}

	public final RegisterModule getRegisterModule() {
		return registerModule;
	}

	public final SettingsModule getSettingsModule() {
		return settingsModule;
	}

	public final WhitelistModule getWhitelistModule() {
		return whitelistModule;
	}

	public void update() {
		final long currentTime = System.currentTimeMillis();

		if (settingsModule.meet(this.currentPPS, this.currentCPS, this.currentJPS)) {
			final Iterator<BotPlayer> pendingIterator = settingsModule.getPending().iterator();

			synchronized (pendingIterator) {
				while (pendingIterator.hasNext()) {
					try {
						final BotPlayer botPlayer = pendingIterator.next();

						if (botPlayer.isSettings()) {
							pendingIterator.remove();
						} else if (currentTime - botPlayer.getLastConnection() >= settingsModule.getDelay()) {
							for (final ProxiedPlayer proxiedPlayer : botPlayer.getPlayers()) {
								final String language = BungeeUtil.getLanguage(proxiedPlayer, "en");

								if (proxiedPlayer.isConnected() && settingsModule.check(proxiedPlayer)) {
									new Punish(plugin, this, language, settingsModule, proxiedPlayer, null);
								}
							}

							pendingIterator.remove();
						}
					} catch (final Exception exception) {
					}
				}
			}
		}

		final Iterator<BotPlayer> offlineIterator = playerModule.getOfflinePlayers().iterator();

		synchronized (offlineIterator) {
			while (offlineIterator.hasNext()) {
				final BotPlayer botPlayer = offlineIterator.next();

				try {
					if (currentTime - botPlayer.getLastConnection() > playerModule.getCacheTime()) {
						offlineIterator.remove();
						playerModule.remove(botPlayer);
					}
				} catch (final Exception exception) {
				}
			}
		}

		this.lastPPS = currentPPS;
		this.lastCPS = currentCPS;
		this.lastJPS = currentJPS;
		this.currentPPS = 0;
		this.currentCPS = 0;
		this.currentJPS = 0;
	}

	public int getLastPPS() {
		return lastPPS;
	}

	public int getLastCPS() {
		return lastCPS;
	}

	public int getLastJPS() {
		return lastJPS;
	}

	public int getCurrentPPS() {
		return currentPPS;
	}

	public void setCurrentPPS(final int currentPPS) {
		this.currentPPS = currentPPS;
	}

	public int getCurrentCPS() {
		return currentCPS;
	}

	public void setCurrentCPS(final int currentCPS) {
		this.currentCPS = currentCPS;
	}

	public int getCurrentJPS() {
		return currentJPS;
	}

	public void setCurrentJPS(final int currentJPS) {
		this.currentJPS = currentJPS;
	}
}