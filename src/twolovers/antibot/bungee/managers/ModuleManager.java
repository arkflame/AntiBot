package twolovers.antibot.bungee.managers;

import twolovers.antibot.bungee.modules.*;
import twolovers.antibot.bungee.utils.ConfigurationUtil;

import java.util.HashMap;
import java.util.Map;

public class ModuleManager {
	private final Map<String, Long> lastPing = new HashMap<>();
	private final Map<String, Long> lastConnection = new HashMap<>();
	private final Map<String, Integer> PPS = new HashMap<>();
	private final Map<String, Integer> CPS = new HashMap<>();
	private final Map<String, Integer> JPS = new HashMap<>();
	private String lastNickname = "AAAAAAAAAAAAAAAA";
	private AccountsModule accountsModule;
	private BlacklistModule blacklistModule;
	private ConsoleFilterModule consoleFilterModule;
	private FastChatModule fastChatModule;
	private ReconnectModule reconnectModule;
	private MessagesModule messagesModule;
	private NicknameModule nicknameModule;
	private NotificationsModule notificationsModule;
	private RateLimitModule rateLimitModule;
	private RegisterModule registerModule;
	private SettingsModule settingsModule;
	private WhitelistModule whitelistModule;
	private int lastPPS = 0;
	private int lastCPS = 0;
	private int lastJPS = 0;
	private int totalPPS = 0;
	private int totalCPS = 0;
	private int totalJPS = 0;

	public ModuleManager(final ConfigurationUtil configurationUtil) {
		this.accountsModule = new AccountsModule(configurationUtil, this);
		this.blacklistModule = new BlacklistModule(configurationUtil, this);
		this.consoleFilterModule = new ConsoleFilterModule(configurationUtil);
		this.fastChatModule = new FastChatModule(configurationUtil, this);
		this.reconnectModule = new ReconnectModule(configurationUtil, this);
		this.messagesModule = new MessagesModule(configurationUtil, this);
		this.nicknameModule = new NicknameModule(configurationUtil, this);
		this.notificationsModule = new NotificationsModule(configurationUtil, this);
		this.rateLimitModule = new RateLimitModule(configurationUtil, this);
		this.registerModule = new RegisterModule(configurationUtil, this);
		this.settingsModule = new SettingsModule(configurationUtil, this);
		this.whitelistModule = new WhitelistModule(configurationUtil, this);
	}

	public final void reload() {
		accountsModule.reload();
		blacklistModule.reload();
		consoleFilterModule.reload();
		fastChatModule.reload();
		reconnectModule.reload();
		messagesModule.reload();
		nicknameModule.reload();
		notificationsModule.reload();
		rateLimitModule.reload();
		settingsModule.reload();
		whitelistModule.reload();
	}

	public final AccountsModule getAccountsModule() {
		return accountsModule;
	}

	public final BlacklistModule getBlacklistModule() {
		return blacklistModule;
	}
	
	public final ConsoleFilterModule getConsoleFilterModule() {
		return consoleFilterModule;
	}

	public final FastChatModule getFastChatModule() {
		return fastChatModule;
	}

	public final ReconnectModule getReconnectModule() {
		return reconnectModule;
	}

	public final MessagesModule getMessagesModule() {
		return messagesModule;
	}

	public final NicknameModule getNicknameModule() {
		return nicknameModule;
	}

	public final NotificationsModule getNotificationsModule() {
		return notificationsModule;
	}

	public final RateLimitModule getRateLimitModule() {
		return rateLimitModule;
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
		lastPPS = totalPPS;
		lastCPS = totalCPS;
		lastJPS = totalJPS;
		totalPPS = 0;
		totalCPS = 0;
		totalJPS = 0;
		PPS.clear();
		CPS.clear();
		JPS.clear();
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

	public void addPPS(String ip, int number) {
		totalPPS += number;
		PPS.put(ip, PPS.getOrDefault(ip, 0) + number);
	}

	public void addCPS(String ip, int number) {
		totalCPS += number;
		CPS.put(ip, CPS.getOrDefault(ip, 0) + number);
	}

	public void addJPS(String ip, int number) {
		totalJPS += number;
		JPS.put(ip, JPS.getOrDefault(ip, 0) + number);
	}

	public int getTotalPPS() {
		return totalPPS;
	}

	public int getTotalCPS() {
		return totalCPS;
	}

	public int getTotalJPS() {
		return totalJPS;
	}

	public int getPPS(String ip) {
		return PPS.getOrDefault(ip, 0);
	}

	public int getCPS(String ip) {
		return CPS.getOrDefault(ip, 0);
	}

	public int getJPS(String ip) {
		return JPS.getOrDefault(ip, 0);
	}

	public final long getLastPing(final String ip) {
		return lastPing.getOrDefault(ip, (long) 0);
	}

	public final void setLastPing(final String ip, final long time) {
		lastPing.put(ip, time);
	}

	public final long getLastConnection(final String ip) {
		return lastConnection.getOrDefault(ip, (long) 0);
	}

	public final void setLastConnection(final String ip, final long time) {
		lastConnection.put(ip, time);
	}

	public final String getLastNickname() {
		return lastNickname;
	}

	public final void setLastNickname(String nickname) {
		lastNickname = nickname;
	}
}
