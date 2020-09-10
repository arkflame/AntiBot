package twolovers.antibot.shared.extendables;

import java.util.Collection;
import java.util.HashSet;

import twolovers.antibot.bungee.instanceables.Conditions;
import twolovers.antibot.bungee.utils.ConfigUtil;
import twolovers.antibot.shared.interfaces.IPunishModule;

public class PunishableModule implements IPunishModule {
    protected Collection<String> punishCommands = new HashSet<>();
    protected Conditions conditions;
    protected boolean enabled = true;

    @Override
    public String getName() {
        // Overriden by parent class
        return "<N/A>";
    }

    @Override
    public void reload(ConfigUtil configUtil) {
        // Overriden by parent class
    }

    @Override
    public Collection<String> getPunishCommands() {
        return punishCommands;
    }

    public boolean meet(final int pps, final int cps, final int jps, final int lastPps, final int lastCps,
            final int lastJps) {
        return this.enabled && (conditions.meet(pps, cps, jps, lastPps, lastCps, lastJps));
    }
}
