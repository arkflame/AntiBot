package twolovers.antibot.bungee.modules;

import net.md_5.bungee.config.Configuration;
import twolovers.antibot.bungee.utils.ConfigurationUtil;

import java.util.List;

public class LogFilterModule {
    private final ConfigurationUtil configurationUtil;
    private boolean logFilterEnabled = true;
    private List<String> filterMessages;

    public LogFilterModule(ConfigurationUtil configurationUtil) {
        this.configurationUtil = configurationUtil;
        reload();
    }

    public final void reload() {
        final Configuration config = configurationUtil.getConfiguration("%datafolder%/config.yml");

        if (config != null) {
            logFilterEnabled = config.getBoolean("consolefilter.enabled");
            filterMessages = config.getStringList("consolefilter.messages");
        }
    }

    public List<String> getFilter() {
        return filterMessages;
    }

    public boolean isCondition() {
        return logFilterEnabled;
    }
}
