package twolovers.antibot.bungee.module;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import twolovers.antibot.bungee.instanceables.BotPlayer;
import twolovers.antibot.bungee.utils.ConfigUtil;
import twolovers.antibot.shared.interfaces.IModule;

public class PlayerModule implements IModule {
    private static final String NAME = "player";
    private final Map<String, BotPlayer> players = new HashMap<>();
    private final Collection<BotPlayer> offlinePlayers = new HashSet<>();
    private int cacheTime = 30000;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void reload(final ConfigUtil configUtil) {
        /* This Reload method doesn't need an implementation */
    }

    public final BotPlayer get(final String hostString) {
        final BotPlayer botPlayer;

        if (players.containsKey(hostString)) {
            botPlayer = players.get(hostString);
        } else {
            botPlayer = new BotPlayer(hostString);

            players.put(hostString, botPlayer);
        }

        return botPlayer;
    }

    public final void setOnline(final BotPlayer botPlayer) {
        offlinePlayers.remove(botPlayer);
    }

    public final void setOffline(final BotPlayer botPlayer) {
        offlinePlayers.add(botPlayer);
    }

    public Collection<BotPlayer> getOfflinePlayers() {
        return offlinePlayers;
    }

    public void remove(final BotPlayer botPlayer) {
        final String hostAddress = botPlayer.getHostAddress();

        players.remove(hostAddress);
        offlinePlayers.remove(botPlayer);
    }

    public int getCacheTime() {
        return cacheTime;
    }
}