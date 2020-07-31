package twolovers.antibot.shared.interfaces;

import twolovers.antibot.bungee.utils.ConfigUtil;

public interface IModule {
	String getName();

	void reload(final ConfigUtil configUtil);
}
