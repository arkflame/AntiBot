package twolovers.antibot.bungee.module;

import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import twolovers.antibot.bungee.instanceables.Conditions;
import twolovers.antibot.bungee.utils.ConfigUtil;
import twolovers.antibot.shared.extendables.PunishableModule;

import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Pattern;

public class NicknameModule extends PunishableModule {
	private static final String NAME = "nickname";
	private Collection<String> blacklist = new HashSet<>();
	private Pattern pattern = Pattern.compile(
			"^(Craft|Beach|Actor|Games|Tower|Elder|Mine|Nitro|Worms|Build|Plays|Hyper|Crazy|Super|_Itz|Slime)(Craft|Beach|Actor|Games|Tower|Elder|Mine|Nitro|Worms|Build|Plays|Hyper|Crazy|Super|_Itz|Slime)(11|50|69|99|88|HD|LP|XD|YT)");
	private String lastNickname = "A";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public final void reload(final ConfigUtil configUtil) {
		final Configuration configYml = configUtil.getConfiguration("%datafolder%/config.yml");
		final int pps = configYml.getInt(NAME + ".conditions.pps", 0);
		final int cps = configYml.getInt(NAME + ".conditions.cps", 0);
		final int jps = configYml.getInt(NAME + ".conditions.jps", 0);

		enabled = configYml.getBoolean(NAME + ".enabled", enabled);
		punishCommands.clear();
		punishCommands.addAll(configYml.getStringList(NAME + ".commands"));
		conditions = new Conditions(pps, cps, jps, false);
		blacklist.clear();
		blacklist.addAll(configYml.getStringList(NAME + ".blacklist"));
	}

	public final boolean meet(final int pps, final int cps, final int jps, final int lastPps, final int lastCps,
			final int lastJps) {
		return this.enabled && (conditions.meet(pps, cps, jps, lastPps, lastCps, lastJps));
	}

	public boolean check(final Connection connection) {
		if (connection instanceof ProxiedPlayer) {
			final String name = ((ProxiedPlayer) connection).getName();

			if (!name.equals(lastNickname) && name.length() == lastNickname.length()) {
				return true;
			} else {
				final String lowerName = name.toLowerCase();

				for (final String blacklisted : blacklist) {
					if (lowerName.contains(blacklisted)) {
						return true;
					}
				}

				return pattern.matcher(name).find();
			}
		}

		return false;
	}

	public final String getLastNickname() {
		return lastNickname;
	}

	public final void setLastNickname(String nickname) {
		lastNickname = nickname;
	}
}
