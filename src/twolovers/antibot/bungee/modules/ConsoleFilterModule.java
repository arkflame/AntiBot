package twolovers.antibot.bungee.modules;

import net.md_5.bungee.config.Configuration;
import twolovers.antibot.bungee.utils.ConfigurationUtil;

import java.util.List;

public class ConsoleFilterModule {
    private final ConfigurationUtil configurationUtil;
    private boolean consoleFilterEnabled = false;
    private List<String> filterMessages;

    public ConsoleFilterModule(ConfigurationUtil configurationUtil) {
        this.configurationUtil = configurationUtil;
    }

    public final void reload() {
        final Configuration config = configurationUtil.getConfiguration("%datafolder%/config.yml");

        if (config != null) {
            consoleFilterEnabled = config.getBoolean("consolefilter.enabled");
            filterMessages = config.getStringList("consolefilter.messages");
        }
    }

    public List<String> getFilter() {
        return filterMessages;
    }

    public boolean isConsoleFilterEnabled() {
        return consoleFilterEnabled;
    }
}
