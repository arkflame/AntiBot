package twolovers.antibot.bungee.utils;

import java.util.Locale;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeUtil {
    public static String getLanguage(final ProxiedPlayer proxiedPlayer, final String defaultString) {
        final Locale locale = proxiedPlayer.getLocale();

        if (locale == null) {
            return defaultString;
        } else {
            return locale.toLanguageTag();
        }
    }
}