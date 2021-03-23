package twolovers.antibot.bungee.utils;

import java.util.concurrent.atomic.LongAccumulator;

public class Incoming {
    private final LongAccumulator pps;
    private final LongAccumulator cps;
    private final LongAccumulator jps;

    public Incoming(final int pps, final int cps, final int jps) {
        this.pps = new LongAccumulator(Long::sum, 0L);
        this.pps.accumulate(pps);
        this.cps = new LongAccumulator(Long::sum, 0L);
        this.cps.accumulate(cps);
        this.jps = new LongAccumulator(Long::sum, 0L);
        this.jps.accumulate(jps);
    }

    public Incoming() {
        this(0, 0, 0);
    }

    public void reset() {
        pps.reset();
        cps.reset();
        jps.reset();
    }

    public void addPPS() {
        pps.accumulate(1L);
    }

    public void addCPS() {
        cps.accumulate(1L);
    }

    public void addJPS() {
        jps.accumulate(1L);
    }

    public long getPPS() {
        return pps.get();
    }

    public long getCPS() {
        return cps.get();
    }

    public long getJPS() {
        return jps.get();
    }

	public boolean isGreater(final Incoming incoming) {
		return pps.get() >= incoming.getPPS() && cps.get() >= incoming.getCPS() && jps.get() >= incoming.getJPS();
	}

	public boolean hasGreater(final Incoming incoming) {
		return pps.get() >= incoming.getPPS() || cps.get() >= incoming.getCPS() || jps.get() >= incoming.getJPS();
	}
}
