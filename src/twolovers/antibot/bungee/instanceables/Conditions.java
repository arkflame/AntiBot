package twolovers.antibot.bungee.instanceables;

public class Conditions {
	private final int pps, cps, jps;
	private final boolean oneMeeting;

	public Conditions(final int pps, final int cps, final int jps, final boolean oneMeeting) {
		this.pps = pps;
		this.cps = cps;
		this.jps = jps;
		this.oneMeeting = oneMeeting;
	}

	public boolean meet(final int pps, final int cps, final int jps, final int lastPPS, final int lastCPS,
			final int lastJPS) {
		if (oneMeeting)
			return (pps >= this.pps || cps >= this.cps || jps >= this.jps);
		else
			return ((pps >= this.pps && cps >= this.cps && jps >= this.jps)
					|| (lastPPS >= this.pps && lastCPS >= this.cps && lastJPS >= this.jps));
	}
}
