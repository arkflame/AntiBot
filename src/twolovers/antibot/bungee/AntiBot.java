package twolovers.antibot.bungee;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import twolovers.antibot.bungee.commands.AntibotCommand;
import twolovers.antibot.bungee.listeners.*;
import twolovers.antibot.bungee.utils.ConfigurationUtil;
import twolovers.antibot.bungee.managers.ModuleManager;

import java.util.concurrent.TimeUnit;

public class AntiBot extends Plugin {
	private ModuleManager moduleManager;

	public void onEnable() {
		final ConfigurationUtil configurationUtil = new ConfigurationUtil(this);

		configurationUtil.createConfiguration("%datafolder%/config.yml");
		configurationUtil.createConfiguration("%datafolder%/messages.yml");
		configurationUtil.createConfiguration("%datafolder%/blacklist.yml");
		configurationUtil.createConfiguration("%datafolder%/whitelist.yml");

		moduleManager = new ModuleManager(configurationUtil);

		this.getProxy().getScheduler().schedule(this, moduleManager::update, 1, 1, TimeUnit.SECONDS);

		final PluginManager pluginManager = getProxy().getPluginManager();

		pluginManager.registerListener(this, new ChatListener(moduleManager));
		pluginManager.registerListener(this, new PlayerDisconnectListener(moduleManager));
		pluginManager.registerListener(this, new PostLoginListener(moduleManager));
		pluginManager.registerListener(this, new PreLoginListener(moduleManager));
		pluginManager.registerListener(this, new ProxyPingListener(moduleManager));
		pluginManager.registerListener(this, new ServerSwitchListener(moduleManager));

		pluginManager.registerCommand(this, new AntibotCommand("antibot", moduleManager));
		pluginManager.registerCommand(this, new AntibotCommand("ab", moduleManager));

		moduleManager.reload();
		moduleManager.getWhitelistModule().loadWhitelist();
		moduleManager.getBlacklistModule().loadBlacklist();
		
		if (moduleManager.getConsoleFilterModule().isConsoleFilterEnabled())
            		new LogLoader(moduleManager, this).loadLogFilter();
	}

	public void onDisable() {
		moduleManager.getWhitelistModule().saveWhitelist();
		moduleManager.getBlacklistModule().saveBlacklist();
	}
}
