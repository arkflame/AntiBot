package twolovers.antibot.bungee.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

	public void saveConfiguration(final Configuration configuration, final String file) {
		final String replacedFile = replaceDataFolder(file);

		this.scheduler.runAsync(plugin, () -> {
			try {
				ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, new File(replacedFile));
				logger.info("File '" + replacedFile + "' successfully saved!");
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