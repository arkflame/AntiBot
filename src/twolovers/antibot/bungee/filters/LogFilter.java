package twolovers.antibot.bungee.filters;

import twolovers.antibot.bungee.AntiBot;
import twolovers.antibot.bungee.managers.ModuleManager;

import java.util.logging.Filter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class LogFilter implements CustomFilter {
    private final ModuleManager moduleManager;
    private Logger logger;

    public LogFilter(ModuleManager moduleManager, Main plugin) {
        this.moduleManager = moduleManager;
        logger = plugin.getProxy().getLogger();
    }

    @Override
    public boolean isLoggable(LogRecord record)
    {
        boolean found = false;

        for(String line : moduleManager.getConsoleFilterModule().getFilter())
        {
            if(found)
                break;

            found = record.getMessage().toLowerCase().startsWith(line.toLowerCase());
        }

        return !(found);
    }

    public Filter inject(Logger logger)
    {
        this.logger = logger;
        return inject();
    }

    @Override
    public Filter inject()
    {
        logger.setFilter(this);
        return logger.getFilter();
    }
}
