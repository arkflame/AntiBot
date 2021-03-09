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
import twolovers.antibot.bungee.tasks.AntiBotSecondTask;
import twolovers.antibot.bungee.utils.ConfigUtil;

public class AntiBot extends Plugin {
	private static AntiBot antiBot;
	private ModuleManager moduleManager;
	private ConfigUtil configUtil;
	private boolean running = true;

	public ModuleManager getModuleManager() {
		return moduleManager;
	}

	public static void setInstance(final AntiBot antiBot) {
		AntiBot.antiBot = antiBot;
	}

	public static AntiBot getInstance() {
		return antiBot;
	}

	public boolean isRunning() {
		return running;
	}

	@Override
	public void onEnable() {
		setInstance(this);

		this.configUtil = new ConfigUtil(this);
		reload();

		/* Thread that repeats itself each second */
		new Thread(new AntiBotSecondTask(getLogger(), antiBot, moduleManager)).start();
	}

	public void reload() {
		final Logger logger = getLogger();
		final ProxyServer proxy = this.getProxy();
		final PluginManager pluginManager = proxy.getPluginManager();

		if (configUtil.getConfiguration("%datafolder%/config.yml").getInt("version", 0) != 1) {
			configUtil.deleteConfiguration("%datafolder%/config.yml");
		}

		configUtil.createConfiguration("%datafolder%/config.yml");
		configUtil.createConfiguration("%datafolder%/messages.yml");
		configUtil.createConfiguration("%datafolder%/blacklist.yml");
		configUtil.createConfiguration("%datafolder%/whitelist.yml");
		logger.info("Configurations successfully created!");

		moduleManager = new ModuleManager(this, configUtil);
		moduleManager.reload();
		logger.info("Modules successfully loaded!");

		pluginManager.unregisterListeners(this);
		pluginManager.registerListener(this, new ChatListener(moduleManager));
		pluginManager.registerListener(this, new PlayerDisconnectListener(moduleManager));
		pluginManager.registerListener(this, new PlayerHandshakeListener(moduleManager));
		pluginManager.registerListener(this, new PostLoginListener(moduleManager));
		pluginManager.registerListener(this, new PreLoginListener(moduleManager));
		pluginManager.registerListener(this, new ProxyPingListener(moduleManager));
		pluginManager.registerListener(this, new ServerSwitchListener(moduleManager));
		pluginManager.registerListener(this, new SettingsChangedListener(moduleManager));
		logger.info("Listeners successfully registered!");

		pluginManager.registerCommand(this, new AntibotCommand(this, configUtil, moduleManager));
		logger.info("Commands successfully registered!");
	}

	@Override
	public void onDisable() {
		running = false;

		moduleManager.getBlacklistModule().save(configUtil);
		moduleManager.getRuntimeModule().update();
		moduleManager.getWhitelistModule().save(configUtil);
	}
}