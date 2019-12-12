package twolovers.antibot.bungee.filters;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.log.ConciseFormatter;
import org.apache.logging.log4j.LogManager;
import twolovers.antibot.bungee.managers.ModuleManager;

import java.util.logging.Logger;

public class LogLoader {
    private ModuleManager moduleManager;
    private Plugin plugin;

    public LogLoader(ModuleManager moduleManager, Plugin plugin) {
        this.moduleManager = moduleManager;
        this.plugin = plugin;
    }

    public void loadLogFilter() {
        if (moduleManager.getConsoleFilterModule().isConsoleFilterEnabled()) {
            if (isLog4J()) {
                (new Log4JFilter(moduleManager)).loadLog4JFilter();
            } else {
                loadDefaultFilter();
            }
        }
    }

    private void loadDefaultFilter() {
        Logger logger = BungeeCord.getInstance().getLogger();
        logger.setFilter(record -> {
            boolean canLog = true;
            final String msg = new ConciseFormatter().formatMessage(record).trim();

            if (record.getMessage().isEmpty() || msg.isEmpty())
                canLog = false;

            if (record.getMessage().length() > 2) {
                for (final String word : moduleManager.getConsoleFilterModule().getFilter()) {
                    if (record.getMessage().contains(word) || msg.contains(word)) {
                        canLog = false;
                        break;
                    }
                }
            }
            return canLog;
        });
    }

    private boolean isLog4J() {
        try {
            LogManager.getRootLogger();
            return true;
        } catch (NoClassDefFoundError var2) {
            return false;
        }
    }
}
