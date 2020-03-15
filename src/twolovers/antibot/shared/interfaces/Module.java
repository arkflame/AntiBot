package twolovers.antibot.shared.interfaces;

import twolovers.antibot.bungee.utils.ConfigUtil;

public interface Module {
	String getName();

	void reload(final ConfigUtil configUtil);
}
