package twolovers.antibot.bungee.module;

import twolovers.antibot.bungee.utils.Incoming;
import twolovers.antibot.shared.interfaces.IModule;

public class CounterModule implements IModule {
	private final Incoming current = new Incoming();
	private final Incoming last = new Incoming();
	private int totalIncome = 0;
	private int totalBlocked = 0;

    @Override
    public String getName() {
        return "Counter";
    }

    public void update() {
		current.reset();
		last.reset();
		totalIncome = 0;
    }

	public Incoming getCurrent() {
		return current;
	}

	public Incoming getLast() {
		return last;
	}

	public int getTotalIncome() {
		return totalIncome;
	}

	public void addIncoming() {
		totalIncome++;
	}

	public void addTotalBlocked() {
		totalBlocked++;
	}

	public int getTotalBlocked() {
		return totalBlocked;
	}
}
