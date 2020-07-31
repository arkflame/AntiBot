package twolovers.antibot.bungee.module;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import twolovers.antibot.bungee.instanceables.BotPlayer;
import twolovers.antibot.bungee.utils.ConfigUtil;
import twolovers.antibot.shared.interfaces.IModule;

public class PlayerModule implements IModule {
    private final String name = "player";
    private final Map<String, BotPlayer> players = new HashMap<>();
    private final Collection<BotPlayer> offlinePlayers = new HashSet<>();
    private int cacheTime;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void reload(final ConfigUtil configUtil) {
        this.cacheTime = 30000;
    }

    public final BotPlayer get(final String hostString) {
        final BotPlayer botPlayer;

        if (players.containsKey(hostString)) {
            botPlayer = players.get(hostString);
        } else {
            botPlayer = new BotPlayer(hostString);

            this.players.put(hostString, botPlayer);
        }

        return botPlayer;
    }

    public final void setOnline(final BotPlayer botPlayer) {
        if (this.offlinePlayers.contains(botPlayer)) {
            this.offlinePlayers.remove(botPlayer);
        }
    }

    public final void setOffline(final BotPlayer botPlayer) {
        if (!this.offlinePlayers.contains(botPlayer)) {
            this.offlinePlayers.add(botPlayer);
        }
    }

    public Collection<BotPlayer> getOfflinePlayers() {
        return this.offlinePlayers;
    }

    public void remove(final BotPlayer botPlayer) {
        final String hostAddress = botPlayer.getHostAddress();

        this.players.remove(hostAddress);
        this.offlinePlayers.remove(botPlayer);
    }

    public long getCacheTime() {
        return cacheTime;
    }
}