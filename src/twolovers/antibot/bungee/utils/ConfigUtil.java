package twolovers.antibot.bungee.utils;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class ConfigUtil {
	final private Plugin plugin;

	public ConfigUtil(final Plugin plugin) {
		this.plugin = plugin;
	}

	public Configuration getConfiguration(String file) {
		final File dataFolder = plugin.getDataFolder();

		file = file.replace("%datafolder%", dataFolder.toPath().toString());

		try {
			return ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(file));
		} catch (IOException e) {
			return new Configuration();
		}
	}

	public void createConfiguration(String file) {
		try {
			final File dataFolder = plugin.getDataFolder();

			file = file.replace("%datafolder%", dataFolder.toPath().toString());

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

				System.out.print(("[%pluginname%] File " + configFile + " has been created!").replace("%pluginname%",
						plugin.getDescription().getName()));
			}
		} catch (final IOException e) {
			System.out.print(("[%pluginname%] Unable to create configuration file!").replace("%pluginname%",
					plugin.getDescription().getName()));
		}
	}

	public void saveConfiguration(final Configuration configuration, final String file) {
		plugin.getProxy().getScheduler().runAsync(plugin, () -> {
			try {
				final File dataFolder = plugin.getDataFolder();

				ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration,
						new File(file.replace("%datafolder%", dataFolder.toPath().toString())));
			} catch (final IOException e) {
				System.out.print(("[%pluginname%] Unable to save configuration file!").replace("%pluginname%",
						plugin.getDescription().getName()));
			}
		});
	}

	public void deleteConfiguration(final String file) {
		plugin.getProxy().getScheduler().runAsync(plugin, () -> {
			final File file1 = new File(file);

			if (file1.exists())
				file1.delete();
		});
	}
}