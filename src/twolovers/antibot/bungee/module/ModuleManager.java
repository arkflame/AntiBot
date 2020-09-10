package twolovers.antibot.bungee.module;

import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Logger;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
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
	private String defaultLanguage;
	private int lastPps = 0, lastCps = 0, lastJps = 0, currentPps = 0, currentCps = 0, currentJps = 0;

	public ModuleManager(final Plugin plugin, final ConfigUtil configUtil) {
		this.plugin = plugin;
		this.proxyServer = plugin.getProxy();
		this.configUtil = configUtil;
		this.modules[0] = new AccountsModule(this);
		this.modules[1] = new BlacklistModule(this);
		this.modules[2] = new FastChatModule(this);
		this.modules[3] = new NicknameModule();
		this.modules[4] = new NotificationsModule(this, plugin.getLogger());
		this.modules[5] = new PlaceholderModule(plugin);
		this.modules[6] = new PlayerModule();
		this.modules[7] = new RateLimitModule(this);
		this.modules[8] = new ReconnectModule(this);
		this.modules[9] = new RegisterModule();
		this.modules[10] = new RuntimeModule();
		this.modules[11] = new SettingsModule();
		this.modules[12] = new WhitelistModule(this);
	}

	public final void reload() {
		try {
			final Configuration config = configUtil.getConfiguration("%datafolder%/config.yml");
			final String lang = config.getString("lang");

			for (final IModule module : modules) {
				module.reload(configUtil);
			}

			if (lang != null) {
				defaultLanguage = lang;
			} else {
				defaultLanguage = "en";
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
		final Logger logger = plugin.getLogger();
		final long currentTime = System.currentTimeMillis();
		final int cacheTime = playerModule.getCacheTime();

		try {
			final Collection<BotPlayer> pendingPlayers = settingsModule.getPending();
			final Collection<BotPlayer> offlinePlayers = playerModule.getOfflinePlayers();
			final long settingsDelay = settingsModule.getDelay();
			final boolean settingsModuleMeet = settingsModule.meet(currentPps, currentCps, currentJps, lastPps, lastCps,
					lastJps);

			for (final BotPlayer botPlayer : new HashSet<>(offlinePlayers)) {
				if (botPlayer == null || currentTime - botPlayer.getLastConnection() > cacheTime) {
					offlinePlayers.remove(botPlayer);
					pendingPlayers.remove(botPlayer);
				} else if (settingsModuleMeet && pendingPlayers.contains(botPlayer)) {
					if (botPlayer.isSettings()) {
						pendingPlayers.remove(botPlayer);
					} else if (currentTime - botPlayer.getLastConnection() >= settingsDelay) {
						for (final String playerName : botPlayer.getAccounts()) {
							final ProxiedPlayer player = proxyServer.getPlayer(playerName);

							if (player != null) {
								final String language = BungeeUtil.getLanguage(player, defaultLanguage);

								new Punish(plugin, this, language, settingsModule, player, null);
								pendingPlayers.remove(botPlayer);
							}
						}
					}
				}
			}
		} catch (final Exception e) {
			logger.warning("AntiBot catched a " + e.getClass().getName() + "! (ModuleManager.java)");
		}

		getRuntimeModule().update();

		lastPps = currentPps;
		lastCps = currentCps;
		lastJps = currentJps;
		this.currentPps = 0;
		this.currentCps = 0;
		this.currentJps = 0;
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

	public int getLastPps() {
		return this.lastPps;
	}

	public int getLastCps() {
		return this.lastCps;
	}

	public int getLastJps() {
		return this.lastJps;
	}

	public int getCurrentPps() {
		return this.currentPps;
	}

	public int getCurrentCps() {
		return this.currentCps;
	}

	public int getCurrentJps() {
		return this.currentJps;
	}

	public void setCurrentPPS(final int currentPPS) {
		this.currentPps = currentPPS;
	}

	public void setCurrentCps(final int currentCPS) {
		this.currentCps = currentCPS;
	}

	public void setCurrentJPS(final int currentJPS) {
		this.currentJps = currentJPS;
	}
}