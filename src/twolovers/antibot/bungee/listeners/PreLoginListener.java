package twolovers.antibot.bungee.listeners;

import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import twolovers.antibot.bungee.instanceables.BotPlayer;
import twolovers.antibot.bungee.instanceables.Punish;
import twolovers.antibot.bungee.module.*;
import twolovers.antibot.bungee.utils.Incoming;

public class PreLoginListener implements Listener {
	private final ModuleManager moduleManager;
	private final AccountsModule accountsModule;
	private final BlacklistModule blacklistModule;
	private final NicknameModule nicknameModule;
	private final PlayerModule playerModule;
	private final RateLimitModule rateLimitModule;
	private final ReconnectModule reconnectModule;
	private final WhitelistModule whitelistModule;

	public PreLoginListener(final ModuleManager moduleManager) {
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

		final String locale = moduleManager.getDefaultLanguage(); // Can't get locale on PreLogin.
		final String ip = connection.getAddress().getHostString();
		final BotPlayer botPlayer = playerModule.get(ip);
		final String name = connection.getName();
		final long currentTimeMillis = System.currentTimeMillis();
		final CounterModule counterModule = moduleManager.getCounterModule();
		final Incoming current = counterModule.getCurrent();
		final Incoming last = counterModule.getLast();

		if (nicknameModule.meet(current, last) && nicknameModule.check(connection)) {
			new Punish(moduleManager, locale, nicknameModule, connection, event);
		} else if (whitelistModule.meet(current, last)) {
			new Punish(moduleManager, locale, blacklistModule, connection, event);

			whitelistModule.setLastLockout(currentTimeMillis);
		} else if (blacklistModule.meet(current, last) && blacklistModule.check(connection)) {
			new Punish(moduleManager, locale, blacklistModule, connection, event);
		} else if (rateLimitModule.meet(botPlayer.getIncoming())) {
			new Punish(moduleManager, locale, rateLimitModule, connection, event);

			blacklistModule.setBlacklisted(ip, true);
		} else if (accountsModule.meet(current, last) && accountsModule.check(connection)) {
			new Punish(moduleManager, locale, accountsModule, connection, event);
		} else if (reconnectModule.meet(current, last) && reconnectModule.check(connection)) {
			botPlayer.setReconnects(botPlayer.getReconnects() + 1);

			new Punish(moduleManager, locale, reconnectModule, connection, event);
		}

		botPlayer.setLastNickname(name);
		nicknameModule.setLastNickname(name);
		botPlayer.setLastConnection(currentTimeMillis);
	}
}
