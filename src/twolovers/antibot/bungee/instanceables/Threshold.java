package twolovers.antibot.bungee.instanceables;

import twolovers.antibot.bungee.utils.Incoming;

public class Threshold {
	// The amount of [PPS/CPS/JPS] required.
	private final Incoming incoming;
	// If only one [PPS/CPS/JPS] value should match.
	private final boolean oneMatch;

	public Threshold(final Incoming incoming, final boolean oneMeeting) {
		this.incoming = incoming;
		this.oneMatch = oneMeeting;
	}

	public boolean meet(final Incoming ...incoming1) {
		if (!oneMatch) {
			for (final Incoming incoming2 : incoming1) {
				if (incoming2.isGreater(incoming)) {
					return true;
				}
			}
		}

		for (final Incoming incoming2 : incoming1) {
			if (incoming2.hasGreater(incoming)) {
				return true;
			}
		}

		return false;
	}
}
