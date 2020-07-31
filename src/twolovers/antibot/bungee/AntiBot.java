package twolovers.antibot.bungee;

import java.util.logging.Logger;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import twolovers.antibot.bungee.commands.AntibotCommand;
import twolovers.antibot.bungee.listeners.ChatListener;
import twolovers.antibot.bungee.listeners.PlayerDisconnectListener;
import twolovers.antibot.bungee.listeners.PlayerHandshakeListener;
import twolovers.antibot.bungee.listeners.PostLoginListener;
import twolovers.antibot.bungee.listeners.PreLoginListener;
import twolovers.antibot.bungee.listeners.ProxyPingListener;
import twolovers.antibot.bungee.listeners.ServerSwitchListener;
import twolovers.antibot.bungee.listeners.SettingsChangedListener;
import twolovers.antibot.bungee.module.ModuleManager;
import twolovers.antibot.bungee.utils.ConfigUtil;

public class AntiBot extends Plugin {
	private static AntiBot antiBot;
	private ModuleManager moduleManager;
	private ConfigUtil configUtil;

	public void onEnable() {
		final Logger logger = this.getLogger();

		AntiBot.antiBot = this;
		this.configUtil = new ConfigUtil(this);
		reload();

		// Thread that repeats itself each second
		final Thread thread = new Thread() {
			@Override
			public void run() {
				final Thread thread = this;

				synchronized (thread) {
					while (!isInterrupted()) {
						try {
							moduleManager.update();
						} catch (final Exception ex1) {
							logger.warning("AntiBot catched a generic exception! (AntiBot.java)");
						}

						try {
							thread.wait(1000);
						} catch (final InterruptedException ex2) {
							logger.warning("AntiBot catched a interrupted exception! (AntiBot.java)");
							thread.start();
						}
					}
				}
			}
		};

		thread.start();
	}

	public void reload() {
		final ProxyServer proxy = this.getProxy();
		final PluginManager pluginManager = proxy.getPluginManager();

		configUtil.createConfiguration("%datafolder%/config.yml");
		configUtil.createConfiguration("%datafolder%/messages.yml");
		configUtil.createConfiguration("%datafolder%/blacklist.yml");
		configUtil.createConfiguration("%datafolder%/whitelist.yml");

		moduleManager = new ModuleManager(this, configUtil);
		moduleManager.reload();

		pluginManager.unregisterListeners(this);
		pluginManager.registerListener(this, new ChatListener(this, moduleManager));
		pluginManager.registerListener(this, new PlayerDisconnectListener(moduleManager));
		pluginManager.registerListener(this, new PlayerHandshakeListener(this, moduleManager));
		pluginManager.registerListener(this, new PostLoginListener(this, moduleManager));
		pluginManager.registerListener(this, new PreLoginListener(this, moduleManager));
		pluginManager.registerListener(this, new ProxyPingListener(this, moduleManager));
		pluginManager.registerListener(this, new ServerSwitchListener(this, moduleManager));
		pluginManager.registerListener(this, new SettingsChangedListener(this, moduleManager));

		pluginManager.registerCommand(this, new AntibotCommand(this, configUtil, moduleManager));
	}

	public void onDisable() {
		moduleManager.getBlacklistModule().save(configUtil);
		moduleManager.getRuntimeModule().update();
		moduleManager.getWhitelistModule().save(configUtil);
	}

	public ModuleManager getModuleManager() {
		return moduleManager;
	}

	public static AntiBot getInstance() {
		return antiBot;
	}
}