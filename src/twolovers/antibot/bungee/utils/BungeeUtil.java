package twolovers.antibot.bungee.utils;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Locale;

public class BungeeUtil {
    public static String getLanguage(final ProxiedPlayer proxiedPlayer, final String defaultString) {
        final Locale locale = proxiedPlayer.getLocale();

        if (locale != null) return locale.toLanguageTag();

        return defaultString;

    }
}