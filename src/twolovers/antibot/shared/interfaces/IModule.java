package twolovers.antibot.shared.interfaces;

import twolovers.antibot.bungee.utils.ConfigUtil;

public interface IModule {
	String getName();

	default void reload(final ConfigUtil configUtil) {

	}
}
