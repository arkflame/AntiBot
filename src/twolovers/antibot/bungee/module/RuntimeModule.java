package twolovers.antibot.bungee.module;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import net.md_5.bungee.config.Configuration;
import twolovers.antibot.bungee.utils.ConfigUtil;
import twolovers.antibot.shared.interfaces.IModule;

public class RuntimeModule implements IModule {
	private final Runtime runtime = Runtime.getRuntime();
	private final Collection<String> blacklisted = new HashSet<>(), addCommands = new HashSet<>(),
			removeCommands = new HashSet<>();
	private final String name = "runtime";
	private long lastUpdateTime = 0;
	private int time = 20000;
	private boolean enabled = true;

	@Override
	public final String getName() {
		return name;
	}

	@Override
	public final void reload(final ConfigUtil configUtil) {
		final Configuration configYml = configUtil.getConfiguration("%datafolder%/config.yml");

		enabled = configYml.getBoolean(name + ".enabled", enabled);
		time = configYml.getInt(name + ".time", time);

		if (configYml.contains(name + ".add")) {
			addCommands.addAll(configYml.getStringList(name + ".add"));
		}

		if (configYml.contains(name + ".remove")) {
			removeCommands.addAll(configYml.getStringList(name + ".remove"));
		}
	}

	public void update() {
		if (enabled) {
			final long currentTime = System.currentTimeMillis();

			if (time != -1 && currentTime - lastUpdateTime > time) {
				lastUpdateTime = currentTime;

				for (final String address : new HashSet<>(blacklisted)) {
					try {
						removeBlacklisted(address);
					} catch (final IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public void addBlacklisted(final String address) throws IOException {
		if (enabled && !blacklisted.contains(address)) {
			for (final String command : addCommands) {
				runtime.exec(command.replace("%address%", address));
			}

			blacklisted.add(address);
		}
	}

	public void removeBlacklisted(final String address) throws IOException {
		if (enabled && blacklisted.contains(address)) {
			for (final String command : removeCommands) {
				runtime.exec(command.replace("%address%", address));
			}

			blacklisted.remove(address);
		}
	}
}
