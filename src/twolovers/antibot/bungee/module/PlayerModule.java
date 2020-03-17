package twolovers.antibot.bungee.module;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import net.md_5.bungee.config.Configuration;
import twolovers.antibot.bungee.instanceables.BotPlayer;
import twolovers.antibot.bungee.utils.ConfigUtil;
import twolovers.antibot.shared.interfaces.Module;

public class PlayerModule implements Module {
    private final String name = "player";
    private final Map<String, BotPlayer> players = new HashMap<>();
    private final Collection<BotPlayer> offlinePlayers = new HashSet<>();
    private int cacheTime;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void reload(ConfigUtil configUtil) {
        final Configuration configYml = configUtil.getConfiguration("%datafolder%/config.yml");

        this.cacheTime = configYml.getInt(name + ".cache_time", 15000);
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
        this.offlinePlayers.remove(botPlayer);
    }

    public final void setOffline(final BotPlayer botPlayer) {
        this.offlinePlayers.add(botPlayer);
    }

    public Collection<BotPlayer> getOfflinePlayers() {
        return this.offlinePlayers;
    }

    public void remove(final BotPlayer botPlayer) {
        this.players.remove(botPlayer.getHostAddress());
    }

    public long getCacheTime() {
        return cacheTime;
    }
}