package twolovers.antibot.bungee.module;

import io.netty.util.internal.ConcurrentSet;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.config.Configuration;
import twolovers.antibot.bungee.AntiBot;
import twolovers.antibot.bungee.utils.ConfigUtil;
import twolovers.antibot.shared.interfaces.IModule;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

public class RuntimeModule implements IModule {
	private final Runtime runtime = Runtime.getRuntime();
	private final Collection<String> blacklisted = new ConcurrentSet<>(), addCommands = new ConcurrentSet<>(),
			removeCommands = new ConcurrentSet<>(); // HashSet to ConcurrentSet to prevent ConcurrentModificationException.
	private static final String NAME = "runtime";
	private long lastUpdateTime = 0;
	private int time = 20000;
	private boolean enabled = true;

	@Override
	public final String getName() {
		return NAME;
	}

	@Override
	public final void reload(final ConfigUtil configUtil) {
		final Configuration configYml = configUtil.getConfiguration("%datafolder%/config.yml");

		enabled = configYml.getBoolean(NAME + ".enabled", enabled);
		time = configYml.getInt(NAME + ".time", time);

		if (configYml.contains(NAME + ".add")) {
			addCommands.addAll(configYml.getStringList(NAME + ".add"));
		}

		if (configYml.contains(NAME + ".remove")) {
			removeCommands.addAll(configYml.getStringList(NAME + ".remove"));
		}
	}

	public void update() {
		if (enabled && !removeCommands.isEmpty()) {
			final long currentTime = System.currentTimeMillis();

			if (time != -1 && currentTime - lastUpdateTime > time) {
				lastUpdateTime = currentTime;

				for (final String address : new HashSet<>(blacklisted)) {
					removeBlacklisted(address);
				}
			}
		}
	}

	public void addBlacklisted(final String address)  {
		if (enabled && !blacklisted.contains(address)) {
			ProxyServer.getInstance().getScheduler().runAsync(AntiBot.getInstance(), () -> { // async to prevent errors when there's a lot of ip to be banned.
			for (final String command : addCommands) {
				try {
					runtime.exec(command.replace("%address%", address).replace("%time%", String.valueOf(time)));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			});
			blacklisted.add(address);
		}
	}

	public void removeBlacklisted(final String address) {
		if (enabled && blacklisted.contains(address)) {
			ProxyServer.getInstance().getScheduler().runAsync(AntiBot.getInstance(), () -> { // async to prevent errors when there's a lot of ip to be banned.
				for (final String command : removeCommands) {
					if (!command.isEmpty()) {
						try {
							runtime.exec(command.replace("%address%", address).replace("%time%", String.valueOf(time)));
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			});

			blacklisted.remove(address);
		}
	}
}
