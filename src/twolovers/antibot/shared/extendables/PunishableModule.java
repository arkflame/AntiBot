package twolovers.antibot.shared.extendables;

import java.util.Collection;
import java.util.HashSet;

import net.md_5.bungee.config.Configuration;
import twolovers.antibot.bungee.instanceables.Threshold;
import twolovers.antibot.bungee.utils.ConfigUtil;
import twolovers.antibot.shared.interfaces.IPunishModule;

public class PunishableModule implements IPunishModule {
    protected Collection<String> punishCommands = new HashSet<>();
    protected Threshold thresholds;
    protected String name = "<N/A>";
    protected boolean enabled = true;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void reload(final ConfigUtil configUtil) {
        final Configuration configYml = configUtil.getConfiguration("%datafolder%/config.yml");
        final int pps = configYml.getInt(name + ".threshold.pps", 0);
		final int cps = configYml.getInt(name + ".threshold.cps", 0);
        final int jps = configYml.getInt(name + ".threshold.jps", 0);
        
        enabled = configYml.getBoolean(name + ".enabled", enabled);
        thresholds = new Threshold(pps, cps, jps, false);
    }

    @Override
    public Collection<String> getPunishCommands() {
        return punishCommands;
    }

    public boolean meet(final int pps, final int cps, final int jps, final int lastPps, final int lastCps,
            final int lastJps) {
        return this.enabled && (thresholds.meet(pps, cps, jps, lastPps, lastCps, lastJps));
    }
}
