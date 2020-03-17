package twolovers.antibot.bungee.commands;

import java.util.concurrent.atomic.AtomicInteger;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import twolovers.antibot.bungee.AntiBot;
import twolovers.antibot.bungee.module.BlacklistModule;
import twolovers.antibot.bungee.module.ModuleManager;
import twolovers.antibot.bungee.module.NotificationsModule;
import twolovers.antibot.bungee.module.PlaceholderModule;
import twolovers.antibot.bungee.module.WhitelistModule;
import twolovers.antibot.bungee.utils.ConfigUtil;

public class AntibotCommand extends Command {
	private final AntiBot antiBot;
	private final ConfigUtil configUtil;
	private final ModuleManager moduleManager;

	public AntibotCommand(final AntiBot antiBot, final ConfigUtil configUtil, final ModuleManager moduleManager) {
		super("antibot", "", "ab");
		this.antiBot = antiBot;
		this.configUtil = configUtil;
		this.moduleManager = moduleManager;
	}

	@Override
	public void execute(final CommandSender commandSender, final String[] args) {
		final PlaceholderModule placeholderModule = moduleManager.getPlaceholderModule();
		final BlacklistModule blacklistModule = moduleManager.getBlacklistModule();
		final WhitelistModule whitelistModule = moduleManager.getWhitelistModule();
		final ProxiedPlayer proxiedPlayer;
		final String address;
		final String locale;

		if (commandSender instanceof ProxiedPlayer) {
			proxiedPlayer = (ProxiedPlayer) commandSender;
			address = proxiedPlayer.getAddress().getHostString();
			locale = proxiedPlayer.getLocale().toLanguageTag();
		} else {
			proxiedPlayer = null;
			address = "127.0.0.1";
			locale = "en";
		}

		if (args.length > 0) {
			switch (args[0].toLowerCase()) {
				case "notify": {
					if (proxiedPlayer != null) {
						final NotificationsModule notificationsModule = moduleManager.getNotificationsModule();
						final boolean hasNotifications = notificationsModule.hasNotifications(proxiedPlayer);

						notificationsModule.setNotifications(proxiedPlayer, !hasNotifications);

						if (!hasNotifications)
							commandSender.sendMessage(new TextComponent(placeholderModule.replacePlaceholders(locale,
									"%notification_enabled%", address, "", new AtomicInteger(1))));
						else
							commandSender.sendMessage(new TextComponent(placeholderModule.replacePlaceholders(locale,
									"%notification_disabled%", address, "", new AtomicInteger(1))));
					} else {
						commandSender.sendMessage(new TextComponent(placeholderModule.replacePlaceholders(locale,
								"%error_console%", address, "", new AtomicInteger(1))));
					}
					break;
				}
				case "reload": {
					antiBot.reload();
					commandSender.sendMessage(new TextComponent(placeholderModule.replacePlaceholders(locale,
							"%reload%", address, "", new AtomicInteger(1))));
					break;
				}
				case "stats": {
					commandSender.sendMessage(new TextComponent(placeholderModule.replacePlaceholders(locale, "%stats%",
							address, "", new AtomicInteger(1))));
					break;
				}
				case "blacklist": {
					if (args.length == 2) {
						if (args[1].equalsIgnoreCase("save")) {
							blacklistModule.save(configUtil);
							commandSender
									.sendMessage(new TextComponent(ChatColor.GREEN + "The blacklist has been saved!"));
						} else if (args[1].equalsIgnoreCase("load")) {
							blacklistModule.load(configUtil);
							commandSender
									.sendMessage(new TextComponent(ChatColor.GREEN + "The blacklist has been loaded!"));
						} else {
							commandSender.sendMessage(new TextComponent(ChatColor.RED + "/blacklist <load/save>"));
						}
					} else if (args.length == 3) {
						if (args[1].equalsIgnoreCase("add")) {
							final String ip = args[2];
							blacklistModule.setBlacklisted(ip, true);
							commandSender
									.sendMessage(new TextComponent(ChatColor.GREEN + ip + " added to the blacklist!"));
						} else if (args[1].equalsIgnoreCase("remove")) {
							final String ip = args[2];
							blacklistModule.setBlacklisted(ip, false);
							commandSender.sendMessage(
									new TextComponent(ChatColor.GREEN + ip + " removed from the blacklist!"));
						} else {
							commandSender
									.sendMessage(new TextComponent(ChatColor.RED + "/blacklist <add/remove> <ip>"));
						}
					} else {
						commandSender.sendMessage(new TextComponent(ChatColor.RED + "/blacklist <load/save>\n"
								+ ChatColor.RED + "/blacklist <add/remove> <ip>"));
					}

					break;
				}
				case "whitelist": {
					if (args.length == 2) {
						if (args[1].equalsIgnoreCase("save")) {
							whitelistModule.save(configUtil);
							commandSender
									.sendMessage(new TextComponent(ChatColor.GREEN + "The whitelist has been saved!"));
						} else if (args[1].equalsIgnoreCase("load")) {
							whitelistModule.load(configUtil);
							commandSender
									.sendMessage(new TextComponent(ChatColor.GREEN + "The whitelist has been loaded!"));
						} else {
							commandSender.sendMessage(new TextComponent(ChatColor.RED + "/whitelist <load/save>"));
						}
					} else if (args.length == 3) {
						if (args[1].equalsIgnoreCase("add")) {
							final String ip = args[2];
							whitelistModule.setWhitelisted(ip, true);
							commandSender
									.sendMessage(new TextComponent(ChatColor.GREEN + ip + " added to the whitelist!"));
						} else if (args[1].equalsIgnoreCase("remove")) {
							final String ip = args[2];
							whitelistModule.setWhitelisted(ip, false);
							commandSender.sendMessage(
									new TextComponent(ChatColor.GREEN + ip + " removed from the whitelist!"));
						} else {
							commandSender
									.sendMessage(new TextComponent(ChatColor.RED + "/whitelist <add/remove> <ip>"));
						}
					} else {
						commandSender.sendMessage(new TextComponent(ChatColor.RED + "/whitelist <load/save>\n"
								+ ChatColor.RED + "/whitelist <add/remove> <ip>"));
					}

					break;
				}
				default: {
					commandSender.sendMessage(new TextComponent(placeholderModule.replacePlaceholders(locale,
							"%error_command%", address, "", new AtomicInteger(1))));
					break;
				}
			}
		} else {
			commandSender.sendMessage(new TextComponent(
					placeholderModule.replacePlaceholders(locale, "%help%", address, "", new AtomicInteger(1))));
		}
	}
}