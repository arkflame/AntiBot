package twolovers.antibot.bungee.modules;

import net.md_5.bungee.config.Configuration;
import twolovers.antibot.bungee.managers.ModuleManager;
import twolovers.antibot.bungee.utils.ConfigurationUtil;

import java.util.HashMap;
import java.util.Map;

public class RateLimitModule {
	private final ConfigurationUtil configurationUtil;
	private final ModuleManager moduleManager;
	private final Map<String, Integer> online = new HashMap<>();
	private int maxOnline = 3;
	private boolean rateLimitEnabled = true;
	private long rateLimitThrottle = 800;
	private String rateLimitKickMessage = "";
	private int PPSCondition = 999999;
	private int CPSCondition = 999999;
	private int JPSCondition = 999999;

	public RateLimitModule(ConfigurationUtil configurationUtil, ModuleManager moduleManager) {
		this.configurationUtil = configurationUtil;
		this.moduleManager = moduleManager;
		reload();
	}

	public final void reload() {
		final Configuration configYml = this.configurationUtil.getConfiguration("%datafolder%/config.yml");

		if (configYml != null) {
			rateLimitEnabled = configYml.getBoolean("ratelimit.enabled");
			maxOnline = configYml.getInt("ratelimit.max_online");
			rateLimitThrottle = configYml.getLong("ratelimit.throttle");
			rateLimitKickMessage = configYml.getString("ratelimit.kick_message").replace("&", "\u00A7");

			for (String string : configYml.getStringList("ratelimit.conditions")) {
				String[] strings = string.split("([|])");
				int value = Integer.parseInt(strings[0]);
				String type = strings[1];

				if (type.equals("PPS")) {
					PPSCondition = value;
				} else if (type.equals("CPS")) {
					CPSCondition = value;
				} else {
					JPSCondition = value;
				}
			}
		}
	}

	public final int getMaxOnline() {
		return maxOnline;
	}

	public final int getOnline(final String ip) {
		return online.getOrDefault(ip, 0);
	}

	public final void addOnline(final String ip, final int number) {
		online.put(ip, online.getOrDefault(ip, 0) + number);
	}

	public final void removeOnline(final String ip, final int number) {
		online.put(ip, online.getOrDefault(ip, 1) - number);

		if (online.get(ip) < 1)
			online.remove(ip);
	}

	public boolean isCondition(String ip) {
		return rateLimitEnabled && moduleManager.getPPS(ip) >= PPSCondition || moduleManager.getCPS(ip) >= CPSCondition || moduleManager.getJPS(ip) >= JPSCondition;
	}

	public long getRateLimitThrottle() {
		return rateLimitThrottle;
	}

	public String getKickMessage() {
		return rateLimitKickMessage;
	}
}
