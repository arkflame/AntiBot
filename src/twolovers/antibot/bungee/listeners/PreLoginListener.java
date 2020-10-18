package twolovers.antibot.bungee.listeners;

import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import twolovers.antibot.bungee.instanceables.BotPlayer;
import twolovers.antibot.bungee.instanceables.Punish;
import twolovers.antibot.bungee.module.*;

public class PreLoginListener implements Listener {
	private final Plugin plugin;
	private final ModuleManager moduleManager;
	private final AccountsModule accountsModule;
	private final BlacklistModule blacklistModule;
	private final NicknameModule nicknameModule;
	private final PlayerModule playerModule;
	private final RateLimitModule rateLimitModule;
	private final ReconnectModule reconnectModule;
	private final WhitelistModule whitelistModule;

	public PreLoginListener(final Plugin plugin, final ModuleManager moduleManager) {
		this.plugin = plugin;
		this.moduleManager = moduleManager;
		this.accountsModule = moduleManager.getAccountsModule();
		this.blacklistModule = moduleManager.getBlacklistModule();
		this.nicknameModule = moduleManager.getNicknameModule();
		this.rateLimitModule = moduleManager.getRateLimitModule();
		this.playerModule = moduleManager.getPlayerModule();
		this.reconnectModule = moduleManager.getReconnectModule();
		this.whitelistModule = moduleManager.getWhitelistModule();
	}

	@EventHandler(priority = Byte.MIN_VALUE)
	public void onPreLogin(final PreLoginEvent event) {
		if (event.isCancelled()) {
			return;
		}

		final PendingConnection connection = event.getConnection();

		if (whitelistModule.check(connection)) {
			return;
		}

		final String locale = "en"; // Can't get locale on PreLogin.
		final String ip = connection.getAddress().getHostString();
		final BotPlayer botPlayer = playerModule.get(ip);
		final String name = connection.getName();
		final long currentTimeMillis = System.currentTimeMillis();
		final int currentPps = moduleManager.getCurrentPps();
		final int currentCps = moduleManager.getCurrentCps();
		final int currentJps = moduleManager.getCurrentJps();
		final int lastPps = moduleManager.getLastPps();
		final int lastCps = moduleManager.getLastCps();
		final int lastJps = moduleManager.getLastJps();

		if (nicknameModule.meet(currentPps, currentCps, currentJps, lastPps, lastCps, lastJps)
				&& nicknameModule.check(connection)) {
			new Punish(plugin, moduleManager, locale, nicknameModule, connection, event);
		} else if (whitelistModule.meet(currentPps, currentCps, currentJps, lastPps, lastCps, lastJps)) {
			new Punish(plugin, moduleManager, locale, blacklistModule, connection, event);

			whitelistModule.setLastLockout(currentTimeMillis);
		} else if (blacklistModule.meet(currentPps, currentCps, currentJps, lastPps, lastCps, lastJps)
				&& blacklistModule.check(connection)) {
			new Punish(plugin, moduleManager, locale, blacklistModule, connection, event);
		} else if (rateLimitModule.meet(botPlayer.getPPS(), botPlayer.getCPS(), botPlayer.getJPS(), 0, 0, 0)) {
			new Punish(plugin, moduleManager, locale, rateLimitModule, connection, event);

			blacklistModule.setBlacklisted(ip, true);
		} else if (accountsModule.meet(currentPps, currentCps, currentJps, lastPps, lastCps, lastJps)
				&& accountsModule.check(connection)) {
			new Punish(plugin, moduleManager, locale, accountsModule, connection, event);
		} else if (reconnectModule.meet(currentPps, currentCps, currentJps, lastPps, lastCps, lastJps)
				&& reconnectModule.check(connection)) {
			botPlayer.setReconnects(botPlayer.getReconnects() + 1);

			new Punish(plugin, moduleManager, locale, reconnectModule, connection, event);
		}

		botPlayer.setLastNickname(name);
		nicknameModule.setLastNickname(name);
		botPlayer.setLastConnection(currentTimeMillis);
	}
}
