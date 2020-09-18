package twolovers.antibot.bungee.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import twolovers.antibot.bungee.utils.BungeeUtil;
import twolovers.antibot.bungee.utils.ConfigUtil;

public class AntibotCommand extends Command {
	private final AntiBot antiBot;
	private final ConfigUtil configUtil;
	private final ModuleManager moduleManager;
	private final Pattern ipPattern = Pattern.compile("([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3})");

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
		final String defaultLanguage = moduleManager.getDefaultLanguage();
		final ProxiedPlayer proxiedPlayer;
		final String address;
		final String locale;

		if (commandSender instanceof ProxiedPlayer) {
			proxiedPlayer = (ProxiedPlayer) commandSender;
			address = proxiedPlayer.getAddress().getHostString();
			locale = BungeeUtil.getLanguage(proxiedPlayer, defaultLanguage);
		} else {
			proxiedPlayer = null;
			address = "0.0.0.0";
			locale = defaultLanguage;
		}

		if (args.length > 0 && !args[0].equals("help")) {
			switch (args[0].toLowerCase()) {
				case "notify": {
					final NotificationsModule notificationsModule = moduleManager.getNotificationsModule();

					if (notificationsModule.isEnabled()) {
						if (proxiedPlayer != null) {
							if (commandSender.hasPermission("antibot.notify")
									|| commandSender.hasPermission("antibot.admin")) {
								final boolean hasNotifications = notificationsModule.hasNotifications(proxiedPlayer);

								notificationsModule.setNotifications(proxiedPlayer, !hasNotifications);

								if (!hasNotifications) {
									commandSender
											.sendMessage(TextComponent.fromLegacyText(placeholderModule.setPlaceholders(
													moduleManager, "%notification_enabled%", locale, address)));
								} else {
									commandSender
											.sendMessage(TextComponent.fromLegacyText(placeholderModule.setPlaceholders(
													moduleManager, "%notification_disabled%", locale, address)));
								}
							} else
								commandSender.sendMessage(TextComponent.fromLegacyText(placeholderModule
										.setPlaceholders(moduleManager, "%error_permission%", locale, address)));
						} else {
							commandSender.sendMessage(TextComponent.fromLegacyText(placeholderModule
									.setPlaceholders(moduleManager, "%error_console%", locale, address)));
						}
					} else {
						commandSender.sendMessage(TextComponent.fromLegacyText(
								placeholderModule.setPlaceholders(moduleManager, "%notification_error%", locale)));
					}
					break;
				}
				case "reload": {
					if (commandSender.hasPermission("antibot.admin")) {
						antiBot.reload();
						commandSender.sendMessage(TextComponent.fromLegacyText(
								placeholderModule.setPlaceholders(moduleManager, "%reload%", locale, address)));
					} else
						commandSender.sendMessage(TextComponent.fromLegacyText(placeholderModule
								.setPlaceholders(moduleManager, "%error_permission%", locale, address)));
					break;
				}
				case "stats": {
					if (commandSender.hasPermission("antibot.admin")) {
						commandSender.sendMessage(TextComponent.fromLegacyText(
								placeholderModule.setPlaceholders(moduleManager, "%stats%", locale, address)));
					} else
						commandSender.sendMessage(TextComponent.fromLegacyText(placeholderModule
								.setPlaceholders(moduleManager, "%error_permission%", locale, address)));
					break;
				}
				case "blacklist": {
					if (commandSender.hasPermission("antibot.admin")) {
						if (args.length == 2) {
							if (args[1].equalsIgnoreCase("save")) {
								blacklistModule.save(configUtil);
								commandSender.sendMessage(TextComponent
										.fromLegacyText(ChatColor.GREEN + "The blacklist has been saved!"));
							} else if (args[1].equalsIgnoreCase("load")) {
								blacklistModule.load(configUtil);
								commandSender.sendMessage(TextComponent
										.fromLegacyText(ChatColor.GREEN + "The blacklist has been loaded!"));
							} else {
								commandSender.sendMessage(
										TextComponent.fromLegacyText(ChatColor.RED + "/blacklist <load/save>"));
							}
						} else if (args.length == 3) {
							final String ip = args[2];
							final Matcher matcher = ipPattern.matcher(ip);

							if (args[1].equalsIgnoreCase("add")) {
								blacklistModule.setBlacklisted(ip, true);
								commandSender.sendMessage(TextComponent
										.fromLegacyText(ChatColor.GREEN + ip + " added to the blacklist!"));
								if (matcher.matches()) {
									if (!blacklistModule.isBlacklisted(ip)) {
										blacklistModule.setBlacklisted(ip, true);
										commandSender.sendMessage(TextComponent
												.fromLegacyText(ChatColor.GREEN + ip + " added to the blacklist!"));
									} else
										commandSender.sendMessage(TextComponent
												.fromLegacyText(ChatColor.RED + ip + " is already blacklisted!"));
								} else
									commandSender.sendMessage(
											TextComponent.fromLegacyText(ChatColor.RED + "Enter a valid ip address!"));
							} else if (args[1].equalsIgnoreCase("remove")) {
								blacklistModule.setBlacklisted(ip, false);
								commandSender.sendMessage(TextComponent
										.fromLegacyText(ChatColor.GREEN + ip + " removed from the blacklist!"));
								if (matcher.matches()) {
									if (blacklistModule.isBlacklisted(ip)) {
										blacklistModule.setBlacklisted(ip, false);
										commandSender.sendMessage(TextComponent
												.fromLegacyText(ChatColor.GREEN + ip + " removed from the blacklist!"));
									} else
										commandSender.sendMessage(TextComponent
												.fromLegacyText(ChatColor.RED + ip + " isn't blacklisted!"));
								} else
									commandSender.sendMessage(
											TextComponent.fromLegacyText(ChatColor.RED + "Enter a valid ip address!"));
							} else {
								commandSender.sendMessage(
										TextComponent.fromLegacyText(ChatColor.RED + "/blacklist <add/remove> <ip>"));
							}
						} else {
							commandSender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED
									+ "/blacklist <load/save>\n" + ChatColor.RED + "/blacklist <add/remove> <ip>"));
						}
					} else
						commandSender.sendMessage(TextComponent.fromLegacyText(placeholderModule
								.setPlaceholders(moduleManager, "%error_permission%", locale, address)));

					break;
				}
				case "whitelist": {
					if (commandSender.hasPermission("antibot.admin")) {
						if (args.length == 2) {
							if (args[1].equalsIgnoreCase("save")) {
								whitelistModule.save(configUtil);
								commandSender.sendMessage(TextComponent
										.fromLegacyText(ChatColor.GREEN + "The whitelist has been saved!"));
							} else if (args[1].equalsIgnoreCase("load")) {
								whitelistModule.load(configUtil);
								commandSender.sendMessage(TextComponent
										.fromLegacyText(ChatColor.GREEN + "The whitelist has been loaded!"));
							} else {
								commandSender.sendMessage(
										TextComponent.fromLegacyText(ChatColor.RED + "/whitelist <load/save>"));
							}
						} else if (args.length == 3) {
							final String ip = args[2];
							final Matcher matcher = ipPattern.matcher(ip);

							if (args[1].equalsIgnoreCase("add")) {
								if (matcher.matches()) {
									if (!whitelistModule.isWhitelisted(ip)) {
										blacklistModule.setBlacklisted(ip, false);
										whitelistModule.setWhitelisted(ip, true);
										commandSender.sendMessage(TextComponent
												.fromLegacyText(ChatColor.GREEN + ip + " added to the whitelist!"));
									} else
										commandSender.sendMessage(TextComponent
												.fromLegacyText(ChatColor.RED + ip + " is already whitelisted!"));
								} else
									commandSender.sendMessage(
											TextComponent.fromLegacyText(ChatColor.RED + "Enter a valid ip address!"));
							} else if (args[1].equalsIgnoreCase("remove")) {
								if (matcher.matches()) {
									if (whitelistModule.isWhitelisted(ip)) {
										whitelistModule.setWhitelisted(ip, false);
										commandSender.sendMessage(TextComponent
												.fromLegacyText(ChatColor.GREEN + ip + " removed from the whitelist!"));
									} else
										commandSender.sendMessage(TextComponent
												.fromLegacyText(ChatColor.RED + ip + " isn't whitelisted!"));
								} else
									commandSender.sendMessage(
											TextComponent.fromLegacyText(ChatColor.RED + "Enter a valid ip address!"));
							} else {
								commandSender.sendMessage(
										TextComponent.fromLegacyText(ChatColor.RED + "/whitelist <add/remove> <ip>"));
							}
						} else {
							commandSender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED
									+ "/whitelist <load/save>\n" + ChatColor.RED + "/whitelist <add/remove> <ip>"));
						}
					} else {
						commandSender.sendMessage(TextComponent.fromLegacyText(placeholderModule
								.setPlaceholders(moduleManager, "%error_permission%", locale, address)));
					}

					break;
				}
				default: {
					commandSender.sendMessage(TextComponent.fromLegacyText(
							placeholderModule.setPlaceholders(moduleManager, "%error_command%", locale, address)));
					break;
				}
			}
		} else {
			commandSender.sendMessage(TextComponent
					.fromLegacyText(placeholderModule.setPlaceholders(moduleManager, "%help%", locale, address)));
		}
	}
}