package twolovers.antibot.bungee.filters;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LifeCycle;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;
import twolovers.antibot.bungee.managers.ModuleManager;

public class Log4JFilter implements Filter {
    private final ModuleManager moduleManager;

    public Log4JFilter(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
    }

    private Filter.Result checkMessage(String message) {
        for (String string : moduleManager.getLogFilterModule().getFilter()) {
            if (message.contains(string)) {
                return Result.DENY;
            } else if (message.isEmpty()) {
                return Result.DENY;
            }
        }
        return Filter.Result.NEUTRAL;
    }

    public LifeCycle.State getState() {
        try {
            return LifeCycle.State.STARTED;
        } catch (Exception var2) {
            return null;
        }
    }

    public void initialize() {
    }

    public boolean isStarted() {
        return true;
    }

    public boolean isStopped() {
        return false;
    }

    public void start() {
    }

    public void stop() {
    }

    public Filter.Result filter(LogEvent event) {
        return this.checkMessage(event.getMessage().getFormattedMessage());
    }

    public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object... arg4) {
        return this.checkMessage(message);
    }

    public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4) {
        return this.checkMessage(message);
    }

    public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, Object message, Throwable arg4) {
        return this.checkMessage(message.toString());
    }

    public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, Message message, Throwable arg4) {
        return this.checkMessage(message.getFormattedMessage());
    }

    public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5) {
        return this.checkMessage(message);
    }

    public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6) {
        return this.checkMessage(message);
    }

    public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6, Object arg7) {
        return this.checkMessage(message);
    }

    public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8) {
        return this.checkMessage(message);
    }

    public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9) {
        return this.checkMessage(message);
    }

    public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9, Object arg10) {
        return this.checkMessage(message);
    }

    public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9, Object arg10, Object arg11) {
        return this.checkMessage(message);
    }

    public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9, Object arg10, Object arg11, Object arg12) {
        return this.checkMessage(message);
    }

    public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9, Object arg10, Object arg11, Object arg12, Object arg13) {
        return this.checkMessage(message);
    }

    public Filter.Result getOnMatch() {
        return Filter.Result.DENY;
    }

    public Filter.Result getOnMismatch() {
        return Filter.Result.NEUTRAL;
    }
}
