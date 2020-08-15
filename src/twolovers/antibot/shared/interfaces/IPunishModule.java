package twolovers.antibot.shared.interfaces;

import java.util.Collection;

public interface IPunishModule extends IModule {
	Collection<String> getPunishCommands();
}
