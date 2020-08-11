package twolovers.antibot.bungee.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.logging.Logger;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class ConfigUtil {
	private final Plugin plugin;
	private final Logger logger;
	private final TaskScheduler scheduler;

	public ConfigUtil(final Plugin plugin) {
		this.plugin = plugin;
		this.logger = plugin.getLogger();
		this.scheduler = plugin.getProxy().getScheduler();
	}

	public Configuration getConfiguration(String file) {
		file = replaceDataFolder(file);

		try {
			return ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(file));
		} catch (IOException e) {
			return new Configuration();
		}
	}

	public void createConfiguration(String file) {
		try {
			file = replaceDataFolder(file);

			final File configFile = new File(file);
			if (!configFile.exists()) {
				final String[] files = file.split("/");
				final InputStream inputStream = plugin.getClass().getClassLoader()
						.getResourceAsStream(files[files.length - 1]);
				final File parentFile = configFile.getParentFile();

				if (parentFile != null)
					parentFile.mkdirs();

				if (inputStream != null)
					Files.copy(inputStream, configFile.toPath());
				else
					configFile.createNewFile();

				logger.info("File " + configFile + " has been created!");
			}
		} catch (final IOException e) {
			logger.info("Unable to create configuration file '" + file + "'!");
		}
	}

	public void updateConfiguration(String file) {
		Configuration config = getConfiguration(file);
		int oldVersion = config.getInt("config-version", 0);
		final String[] path = file.split("/");
		final InputStream inputStream = plugin.getClass().getClassLoader()
				.getResourceAsStream(path[path.length - 1]);
		Configuration newConfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(inputStream);
		if (oldVersion != newConfig.getInt("config-version")) {
			config.set("config-version", newConfig.getInt("config-version"));
			config.getKeys().forEach(key -> {
				if (!newConfig.contains(key)) {
					config.set(key, null);
				}
			});
			newConfig.getKeys().forEach(key -> {
				if (!config.contains(key)) {
					config.set(key, newConfig.get(key));
				}
			});
			saveConfiguration(config, file);
		}
	}

	public void saveConfiguration(final Configuration configuration, final String file) {
		final String replacedFile = replaceDataFolder(file);

		this.scheduler.runAsync(plugin, () -> {
			try {
				ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, new File(replacedFile));
			} catch (final IOException e) {
				logger.info("Unable to save configuration file '" + replacedFile + "'!");
			}
		});
	}

	public void deleteConfiguration(final String file) {
		this.scheduler.runAsync(plugin, () -> {
			final File file1 = new File(file);

			if (file1.exists())
				file1.delete();
		});
	}

	private String replaceDataFolder(final String string) {
		final File dataFolder = plugin.getDataFolder();

		return string.replace("%datafolder%", dataFolder.toPath().toString());
	}
}