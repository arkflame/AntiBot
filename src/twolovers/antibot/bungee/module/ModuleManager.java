package twolovers.antibot.bungee.module;

import java.util.Iterator;
import java.util.logging.Logger;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import twolovers.antibot.bungee.instanceables.BotPlayer;
import twolovers.antibot.bungee.instanceables.Punish;
import twolovers.antibot.bungee.utils.BungeeUtil;
import twolovers.antibot.bungee.utils.ConfigUtil;
import twolovers.antibot.shared.interfaces.IModule;

public class ModuleManager {
	private final Plugin plugin;
	private final ProxyServer proxyServer;
	private final ConfigUtil configUtil;
	private final IModule[] modules = new IModule[13];
	private int lastPPS = 0, lastCPS = 0, lastJPS = 0, currentPPS = 0, currentCPS = 0, currentJPS = 0;

	public ModuleManager(final Plugin plugin, final ConfigUtil configUtil) {
		this.plugin = plugin;
		this.proxyServer = plugin.getProxy();
		this.configUtil = configUtil;
		this.modules[0] = new AccountsModule(this);
		this.modules[1] = new BlacklistModule(this);
		this.modules[2] = new FastChatModule(this);
		this.modules[3] = new NicknameModule(this);
		this.modules[4] = new NotificationsModule(this, plugin.getLogger());
		this.modules[5] = new PlaceholderModule(plugin);
		this.modules[6] = new PlayerModule();
		this.modules[7] = new RateLimitModule(this);
		this.modules[8] = new ReconnectModule(this);
		this.modules[9] = new RegisterModule(this);
		this.modules[10] = new RuntimeModule();
		this.modules[11] = new SettingsModule(this);
		this.modules[12] = new WhitelistModule(this);
	}

	public final void reload() {
		try {
			for (final IModule module : modules) {
				module.reload(configUtil);
			}
		} catch (final Exception exception) {
			plugin.getLogger().warning(
					"There was an exception while loading the configuration files, make sure to reset the configuration files before updating the plugin!");
			throw exception;
		}
	}

	public void update() {
		final PlayerModule playerModule = getPlayerModule();
		final SettingsModule settingsModule = getSettingsModule();
		final Iterator<BotPlayer> offlinePlayersIterator = playerModule.getOfflinePlayers().iterator();
		final Logger logger = plugin.getLogger();
		final long currentTime = System.currentTimeMillis(), cacheTime = playerModule.getCacheTime();

		if (settingsModule.meet(this.currentPPS, this.currentCPS, this.currentJPS)) {
			final Iterator<BotPlayer> pendingPlayersIterator = settingsModule.getPending().iterator();
			final long settingsDelay = settingsModule.getDelay();

			try {
				while (pendingPlayersIterator.hasNext()) {
					final BotPlayer botPlayer = pendingPlayersIterator.next();

					if (botPlayer == null || botPlayer.isSettings()) {
						pendingPlayersIterator.remove();
					} else if (currentTime - botPlayer.getLastConnection() >= settingsDelay) {
						for (final String playerName : botPlayer.getAccounts()) {
							final ProxiedPlayer player = proxyServer.getPlayer(playerName);

							if (player.isConnected() && settingsModule.check(player)) {
								final String language = BungeeUtil.getLanguage(player, "en");

								new Punish(plugin, this, language, settingsModule, player, null);
								pendingPlayersIterator.remove();
							}
						}
					}
				}
			} catch (final Exception ex) {
				logger.warning("AntiBot catched a generic exception! (ModuleManager.java)");
			}
		}

		try {
			while (offlinePlayersIterator.hasNext()) {
				final BotPlayer botPlayer = offlinePlayersIterator.next();

				if (botPlayer == null || currentTime - botPlayer.getLastConnection() > cacheTime) {
					offlinePlayersIterator.remove();
				}
			}
		} catch (final Exception ex) {
			logger.warning("AntiBot catched a generic exception! (ModuleManager.java)");
		}

		getRuntimeModule().update();

		lastPPS = currentPPS;
		lastCPS = currentCPS;
		lastJPS = currentJPS;
		this.currentPPS = 0;
		this.currentCPS = 0;
		this.currentJPS = 0;
	}

	public final AccountsModule getAccountsModule() {
		return (AccountsModule) this.modules[0];
	}

	public final BlacklistModule getBlacklistModule() {
		return (BlacklistModule) this.modules[1];
	}

	public final FastChatModule getFastChatModule() {
		return (FastChatModule) this.modules[2];
	}

	public final NicknameModule getNicknameModule() {
		return (NicknameModule) this.modules[3];
	}

	public final NotificationsModule getNotificationsModule() {
		return (NotificationsModule) this.modules[4];
	}

	public PlaceholderModule getPlaceholderModule() {
		return (PlaceholderModule) this.modules[5];
	}

	public PlayerModule getPlayerModule() {
		return (PlayerModule) this.modules[6];
	}

	public final RateLimitModule getRateLimitModule() {
		return (RateLimitModule) this.modules[7];
	}

	public final ReconnectModule getReconnectModule() {
		return (ReconnectModule) this.modules[8];
	}

	public final RegisterModule getRegisterModule() {
		return (RegisterModule) this.modules[9];
	}

	public final RuntimeModule getRuntimeModule() {
		return (RuntimeModule) this.modules[10];
	}

	public final SettingsModule getSettingsModule() {
		return (SettingsModule) this.modules[11];
	}

	public final WhitelistModule getWhitelistModule() {
		return (WhitelistModule) this.modules[12];
	}

	public int getLastPPS() {
		return this.lastPPS;
	}

	public int getLastCPS() {
		return this.lastCPS;
	}

	public int getLastJPS() {
		return this.lastJPS;
	}

	public int getCurrentPPS() {
		return this.currentPPS;
	}

	public int getCurrentCPS() {
		return this.currentCPS;
	}

	public int getCurrentJPS() {
		return this.currentJPS;
	}

	public void setCurrentPPS(final int currentPPS) {
		this.currentPPS = currentPPS;
	}

	public void setCurrentCPS(final int currentCPS) {
		this.currentCPS = currentCPS;
	}

	public void setCurrentJPS(final int currentJPS) {
		this.currentJPS = currentJPS;
	}
}