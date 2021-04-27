package twolovers.antibot.bungee.module;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import twolovers.antibot.bungee.instanceables.BotPlayer;
import twolovers.antibot.bungee.utils.ConfigUtil;
import twolovers.antibot.bungee.utils.Incoming;
import twolovers.antibot.shared.interfaces.IModule;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

public class PlaceholderModule implements IModule {
    private static final String NAME = "placeholder";
    private final String pluginVersion;
    private final Map<String, String> placeholders = new HashMap<>();
    private final Collection<String> locales = new HashSet<>();
    private String defaultLang;

    PlaceholderModule(final Plugin plugin) {
        pluginVersion = plugin.getDescription().getVersion();
    }

    @Override
    public String getName() {
        return NAME;
    }

    private final String setPlaceholders(String string, final String locale) {
        for (Entry<String, String> entry : placeholders.entrySet()) {
            final String key = entry.getKey();
            final String value = entry.getValue();

            if (string.contains(key)) {
                string = string.replace(key, value);
                continue;
            }

            if (locale == null || locale.length() < 1) continue;

            final String keyLocaleReplaced = key.replace("%" + locale + "_", "%");

            if (string.contains(keyLocaleReplaced)) {
                string = setPlaceholders(string.replace(keyLocaleReplaced, value), locale);
            }
        }

        return string;
    }

    public final String setPlaceholders(final ModuleManager moduleManager, String string, final String locale,
                                        final String address, final String checkName) {

        if (locales.contains(locale)) {
            string = setPlaceholders(string, locale);
        } else if (locale != null && locale.length() > 2 && locales.contains(locale.substring(0, 2))) {
            string = setPlaceholders(string, locale.substring(0, 2));
        } else {
            string = setPlaceholders(string, defaultLang);
        }

        if (moduleManager != null) {
            if (address != null) {
                final ReconnectModule reconnectModule = moduleManager.getReconnectModule();
                final PlayerModule playerModule = moduleManager.getPlayerModule();
                final BotPlayer botPlayer = playerModule.get(address);

                if (botPlayer != null) {
                    final Incoming incoming = botPlayer.getIncoming();
                    final int reconnects = botPlayer.getReconnects(), timesConnect = reconnectModule.getTimesConnect(),
                            reconnectTimes = reconnects > timesConnect ? 0 : timesConnect - reconnects;

                    string = string.replace("%reconnect_times%", String.valueOf(reconnectTimes))
                            .replace("%addresspps%", String.valueOf(incoming.getPPS()))
                            .replace("%addresscps%", String.valueOf(incoming.getCPS()))
                            .replace("%addressjps%", String.valueOf(incoming.getJPS())).replace("%address%", address);
                }
            }

            final CounterModule counterModule = moduleManager.getCounterModule();
            final Incoming current = counterModule.getCurrent();
            final Incoming last = counterModule.getLast();

            string = string.replace("%lastpps%", String.valueOf(last.getPPS()))
                    .replace("%lastcps%", String.valueOf(last.getCPS()))
                    .replace("%lastjps%", String.valueOf(last.getCPS()))
                    .replace("%currentpps%", String.valueOf(current.getPPS()))
                    .replace("%currentcps%", String.valueOf(current.getCPS()))
                    .replace("%currentjps%", String.valueOf(current.getCPS()))
                    .replace("%currentincoming%", String.valueOf(counterModule.getTotalIncome()))
                    .replace("%totalblocked%", String.valueOf(counterModule.getTotalBlocked()))
                    .replace("%totalbls%", String.valueOf(moduleManager.getBlacklistModule().getSize()))
                    .replace("%totalwls%", String.valueOf(moduleManager.getWhitelistModule().getSize()));
        }

        if (checkName != null) {
            string = string.replace("%check%", checkName);
        }

        return ChatColor.translateAlternateColorCodes('&', string.replace("%version%", pluginVersion));
    }

    public final String setPlaceholders(String string) {
        return setPlaceholders(null, string, null, null, null);
    }

    public final String setPlaceholders(final ModuleManager moduleManager, String string) {
        return setPlaceholders(moduleManager, string, null, null, null);
    }

    public final String setPlaceholders(final ModuleManager moduleManager, String string, final String locale) {
        return setPlaceholders(moduleManager, string, locale, null, null);
    }

    public final String setPlaceholders(final ModuleManager moduleManager, String string, final String locale,
                                        final String address) {
        return setPlaceholders(moduleManager, string, locale, address, null);
    }

    private void addSection(final StringBuilder path, final Configuration section) {

        for (String key : section.getKeys()) {
            Object value = section.get(key);

            if (value == null) continue;

            if (value instanceof Configuration) {
                addSection(new StringBuilder(path).append(".").append(key), (Configuration) value);
                continue;
            }

            if (defaultLang != null && value instanceof String) {
                placeholders.put(("%" + path + "." + key + "%").replace(".", "_")
                        .replace("%_", "%"), setPlaceholders((String) value));
            }
        }
    }

    @Override
    public void reload(final ConfigUtil configUtil) {
        final Configuration configYml = configUtil.getConfiguration("%datafolder%/config.yml");
        final Configuration messagesYml = configUtil.getConfiguration("%datafolder%/messages.yml");
        final StringBuilder path = new StringBuilder();

        defaultLang = configYml.getString("lang");
        placeholders.clear();

        for (final String key : messagesYml.getKeys()) {
            if (key.length() > 6) continue;

            locales.add(key);
        }

        addSection(path, messagesYml);
    }
}