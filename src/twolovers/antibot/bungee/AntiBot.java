package twolovers.antibot.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import twolovers.antibot.bungee.commands.AntibotCommand;
import twolovers.antibot.bungee.listeners.*;
import twolovers.antibot.bungee.module.ModuleManager;
import twolovers.antibot.bungee.utils.ConfigUtil;

import java.util.concurrent.TimeUnit;

public class AntiBot extends Plugin {
	private ModuleManager moduleManager;
	private ConfigUtil configUtil;

	public void onEnable() {
		this.configUtil = new ConfigUtil(this);
		this.reload();
	}

	public void reload() {
		final ProxyServer proxy = this.getProxy();
		final TaskScheduler scheduler = proxy.getScheduler();
		final PluginManager pluginManager = proxy.getPluginManager();

		configUtil.createConfiguration("%datafolder%/config.yml");
		configUtil.createConfiguration("%datafolder%/messages.yml");
		configUtil.createConfiguration("%datafolder%/blacklist.yml");
		configUtil.createConfiguration("%datafolder%/whitelist.yml");

		moduleManager = new ModuleManager(this, configUtil);
		moduleManager.reload();

		scheduler.cancel(this);
		scheduler.schedule(this, moduleManager::update, 1, 1, TimeUnit.SECONDS);

		pluginManager.unregisterListeners(this);
		pluginManager.registerListener(this, new ChatListener(this, moduleManager));
		pluginManager.registerListener(this, new PlayerDisconnectListener(moduleManager));
		pluginManager.registerListener(this, new PostLoginListener(this, moduleManager));
		pluginManager.registerListener(this, new PreLoginListener(this, moduleManager));
		pluginManager.registerListener(this, new ProxyPingListener(this, moduleManager));
		pluginManager.registerListener(this, new ServerSwitchListener(this, moduleManager));
		pluginManager.registerListener(this, new SettingsChangedListener(this, moduleManager));

		pluginManager.registerCommand(this, new AntibotCommand(this, configUtil, moduleManager));
	}

	public void onDisable() {
		moduleManager.getWhitelistModule().save(configUtil);
		moduleManager.getBlacklistModule().save(configUtil);
	}
}