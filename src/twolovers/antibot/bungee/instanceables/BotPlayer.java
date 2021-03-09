package twolovers.antibot.bungee.instanceables;

import java.util.Collection;
import java.util.HashSet;

import twolovers.antibot.bungee.utils.Incoming;

public class BotPlayer {
    private final Collection<String> accounts = new HashSet<>();
    private final Collection<String> totalAccounts = new HashSet<>();
    private final String hostString;
    private final Incoming incoming = new Incoming();
    private String lastNickname = "";
    private long lastPing = 0;
    private long lastConnection = 0;
    private long lastUpdate = System.currentTimeMillis();
    private int repings = 0;
    private int reconnects = 0;
    private int switchs = 0;
    private boolean settings = true;

    public BotPlayer(final String hostString) {
        this.hostString = hostString;
    }

    private void updateIncoming() {
        final long currentTimeMillis = System.currentTimeMillis();

        if (currentTimeMillis - this.lastUpdate > 1000) {
            incoming.reset();
            this.lastUpdate = currentTimeMillis;
        }
    }

    public Incoming getIncoming() {
        updateIncoming();

        return incoming;
    }

    public boolean isSettings() {
        return settings;
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

    public Collection<String> getAccounts() {
        return accounts;
    }

    public int getTotalAccounts() {
        return totalAccounts.size();
    }

    public void addAccount(final String playerName) {
        if (!accounts.contains(playerName)) {
            accounts.add(playerName);
            totalAccounts.add(playerName);
        }
    }

    public void removeAccount(final String playerName) {
        accounts.remove(playerName);
    }

    public void setSettings(final boolean settings) {
        this.settings = settings;
    }

    public int getRepings() {
        return this.repings;
    }

    public void setRepings(final int repings) {
        this.repings = repings;
    }

    public int getReconnects() {
        return this.reconnects;
    }

    public void setReconnects(final int reconnects) {
        this.reconnects = reconnects;
    }

    public int getSwitchs() {
        return this.switchs;
    }

    public void addSwitch() {
        this.switchs += 1;
    }

    public void resetSwitchs() {
        this.switchs = 0;
    }

    public String getHostAddress() {
        return hostString;
    }

    public String getLastNickname() {
        return lastNickname;
    }

    public void setLastNickname(final String nickname) {
        if (nickname == null) {
            this.lastNickname = "";
        } else {
            this.lastNickname = nickname;
        }
    }
}