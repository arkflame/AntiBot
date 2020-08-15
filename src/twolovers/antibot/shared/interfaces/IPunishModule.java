package twolovers.antibot.shared.interfaces;

import net.md_5.bungee.api.connection.Connection;

import java.util.Collection;

public interface IPunishModule extends IModule {
	boolean meet(final int pps, final int cps, final int jps);

	boolean check(final Connection connection);

	boolean checkMeet(final int pps, final int cps, final int jps, final Connection connection);

	Collection<String> getPunishCommands();
}
