package twolovers.antibot.bungee.module;

import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import twolovers.antibot.bungee.utils.ConfigUtil;
import twolovers.antibot.bungee.utils.Incoming;
import twolovers.antibot.shared.extendables.PunishableModule;

import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Pattern;

public class NicknameModule extends PunishableModule {
    private final Collection<String> blacklist = new HashSet<>();
    private static final String MCSPAM_WORDS = "(Craft|Beach|Actor|Games|Tower|Elder|Mine|Nitro|Worms|Build|Plays|Hyper|Crazy|Super|_Itz|Slime)";
    private static final String MCSPAM_SUFFIX = "(11|50|69|99|88|HD|LP|XD|YT)";
    private static final Pattern PATTERN = Pattern.compile("^" + MCSPAM_WORDS + MCSPAM_WORDS + MCSPAM_SUFFIX);
    private String lastNickname = "A";

    @Override
    public final void reload(final ConfigUtil configUtil) {
        super.name = "nickname";
        super.reload(configUtil);

        final Configuration configYml = configUtil.getConfiguration("%datafolder%/config.yml");

        punishCommands.clear();
        punishCommands.addAll(configYml.getStringList(name + ".commands"));
        blacklist.clear();
        blacklist.addAll(configYml.getStringList(name + ".blacklist"));
    }

    @Override
    public final boolean meet(final Incoming... incoming) {
        return this.enabled && (thresholds.meet(incoming));
    }

    public boolean check(final Connection connection) {

        if (!(connection instanceof ProxiedPlayer)) {
            return false;
        }

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

            return PATTERN.matcher(name).find();
        }
    }

    public final String getLastNickname() {
        return lastNickname;
    }

    public final void setLastNickname(String nickname) {
        lastNickname = nickname;
    }
}
