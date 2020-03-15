package twolovers.antibot.bungee.instanceables;

import java.util.Collection;
import java.util.HashSet;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BotPlayer {
    private final Collection<ProxiedPlayer> players = new HashSet<>();
    private final Collection<Integer> accounts = new HashSet<>();
    private final String hostString;
    private long lastPing = 0, lastConnection = 0, lastTimeZeroPPS = System.currentTimeMillis(),
            lastTimeZeroCPS = System.currentTimeMillis(), lastTimeZeroJPS = System.currentTimeMillis();
    private int pps = 0, cps = 0, jps = 0, reconnects = 0;
    private boolean settings = false, switched = false;

    public BotPlayer(final String hostString) {
        this.hostString = hostString;
    }

    public boolean isSettings() {
        return settings;
    }

    public int getJPS() {
        final long currentTimeMillis = System.currentTimeMillis();

        if (this.jps == 0) {
            this.lastTimeZeroJPS = currentTimeMillis;
        } else if (currentTimeMillis - this.lastTimeZeroJPS >= 1000) {
            this.jps = 0;
        }

        return jps;
    }

    public void setJPS(final int jps) {
        this.jps = jps;
    }

    public int getCPS() {
        final long currentTimeMillis = System.currentTimeMillis();

        if (this.cps == 0) {
            this.lastTimeZeroCPS = currentTimeMillis;
        } else if (currentTimeMillis - this.lastTimeZeroCPS >= 1000) {
            this.cps = 0;
        }

        return cps;
    }

    public void setCPS(final int cps) {
        this.cps = cps;
    }

    public int getPPS() {
        final long currentTimeMillis = System.currentTimeMillis();

        if (this.pps == 0) {
            this.lastTimeZeroPPS = currentTimeMillis;
        } else if (currentTimeMillis - this.lastTimeZeroPPS >= 1000) {
            this.pps = 0;
        }

        return pps;
    }

    public void setPPS(final int pps) {
        this.pps = pps;
    }

    public long getLastConnection() {
        return lastConnection;
    }

    public void setLastConnection(final long lastConnection) {
        this.lastConnection = lastConnection;
    }

    public long getLastPing() {
        return lastPing;
    }

    public void setLastPing(final long lastPing) {
        this.lastPing = lastPing;
    }

    public Collection<ProxiedPlayer> getPlayers() {
        return players;
    }

    public void addPlayer(final ProxiedPlayer player) {
        if (!this.players.contains(player)) {
            this.players.add(player);
            this.accounts.add(player.getName().hashCode());
        }
    }

    public void removePlayer(final ProxiedPlayer player) {
        this.players.remove(player);
    }

    public boolean isSwitched() {
        return switched;
    }

    public void setSwitched(final boolean switched) {
        this.switched = switched;
    }

    public void setSettings(final boolean settings) {
        this.settings = settings;
    }

    public int getReconnects() {
        return this.reconnects;
    }

    public void setReconnects(final int reconnects) {
        this.reconnects = reconnects;
    }

    public String getHostAddress() {
        return hostString;
    }

    public int getTotalAccounts() {
        return accounts.size();
    }
}