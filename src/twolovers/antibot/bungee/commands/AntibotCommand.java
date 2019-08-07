package twolovers.antibot.bungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import twolovers.antibot.bungee.managers.ModuleManager;
import twolovers.antibot.bungee.modules.*;

public class AntibotCommand extends Command {
	private final ModuleManager moduleManager;
	private final MessagesModule messagesModule;
	private final NotificationsModule notificationsModule;
	private final BlacklistModule blacklistModule;
	private final WhitelistModule whitelistModule;

	public AntibotCommand(String string, ModuleManager moduleManager) {
		super(string);
		this.moduleManager = moduleManager;
		this.messagesModule = moduleManager.getMessagesModule();
		this.notificationsModule = moduleManager.getNotificationsModule();
		this.blacklistModule = moduleManager.getBlacklistModule();
		this.whitelistModule = moduleManager.getWhitelistModule();
	}

	public void execute(CommandSender commandSender, String[] args) {
		final boolean hasAdminPermission = commandSender.hasPermission("antibot.admin");
		final boolean hasNotificationPermission = commandSender.hasPermission("antibot.notifications");

		if (args.length == 1 && args[0].equalsIgnoreCase("notifications") && hasNotificationPermission) {
			if (!(commandSender instanceof ProxiedPlayer)) {
				commandSender.sendMessage(new TextComponent(ChatColor.GREEN + "This cant be used from the console!"));
				return;
			}

			final ProxiedPlayer proxiedPlayer = (ProxiedPlayer) commandSender;

			if (!notificationsModule.isNotifications(proxiedPlayer)) {
				proxiedPlayer.sendMessage(new TextComponent(ChatColor.GREEN + "You have enabled notifications!"));
				notificationsModule.setNotifications(proxiedPlayer, true);

				return;
			} else {
				proxiedPlayer.sendMessage(new TextComponent(ChatColor.GREEN + "You have disabled notifications!"));
				notificationsModule.setNotifications(proxiedPlayer, false);

				return;
			}
		}

		if (!hasAdminPermission) {
			commandSender.sendMessage(new TextComponent(messagesModule.getNoPermission()));
			return;
		}

		if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
			commandSender.sendMessage(new TextComponent(messagesModule.getHelp()));
			return;
		}

		if (args[0].equalsIgnoreCase("reload")) {
			moduleManager.reload();
			messagesModule.reload();
			commandSender.sendMessage(new TextComponent(messagesModule.getReload()));
			return;
		}

		if (args[0].equalsIgnoreCase("stats")) {
			commandSender.sendMessage(new TextComponent(messagesModule.getStats()));
			return;
		}

		if (args[0].equalsIgnoreCase("blacklist")) {
			if (args.length == 2) {
				if (args[1].equalsIgnoreCase("save")) {

					blacklistModule.saveBlacklist();
					commandSender.sendMessage(new TextComponent(ChatColor.GREEN + "The blacklist has been saved!"));
					return;
				} else if (args[1].equalsIgnoreCase("load")) {
					blacklistModule.loadBlacklist();
					commandSender.sendMessage(new TextComponent(ChatColor.GREEN + "The blacklist has been loaded!"));
					return;
				}
			} else if (args.length == 3) {
				if (args[1].equalsIgnoreCase("add")) {
					final String ip = args[2];
					blacklistModule.setBlacklisted(ip, true);
					commandSender.sendMessage(new TextComponent(ChatColor.GREEN + ip + " added to the blacklist!"));
					return;
				} else if (args[1].equalsIgnoreCase("remove")) {
					final String ip = args[2];
					blacklistModule.setBlacklisted(ip, false);
					commandSender.sendMessage(new TextComponent(ChatColor.GREEN + ip + " removed from the blacklist!"));
					return;
				}
			}
		}

		if (args[0].equalsIgnoreCase("whitelist")) {
			if (args.length == 2) {
				if (args[1].equalsIgnoreCase("save")) {
					whitelistModule.saveWhitelist();
					commandSender.sendMessage(new TextComponent(ChatColor.GREEN + "The whitelist has been saved!"));
					return;
				} else if (args[1].equalsIgnoreCase("load")) {
					whitelistModule.loadWhitelist();
					commandSender.sendMessage(new TextComponent(ChatColor.GREEN + "The whitelist has been loaded!"));
					return;
				}
			} else if (args.length == 3) {
				if (args[1].equalsIgnoreCase("add")) {
					final String ip = args[2];
					whitelistModule.setWhitelisted(ip, true);
					commandSender.sendMessage(new TextComponent(ChatColor.GREEN + ip + " added to the whitelist!"));
					return;
				} else if (args[1].equalsIgnoreCase("remove")) {
					final String ip = args[2];
					whitelistModule.setWhitelisted(ip, false);
					commandSender.sendMessage(new TextComponent(ChatColor.GREEN + ip + " removed from the whitelist!"));
					return;
				}
			}
		}


		commandSender.sendMessage(new TextComponent(messagesModule.getUnknownCommand()));
	}
}