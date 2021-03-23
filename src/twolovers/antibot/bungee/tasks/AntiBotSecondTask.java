package twolovers.antibot.bungee.tasks;

import twolovers.antibot.bungee.AntiBot;
import twolovers.antibot.bungee.module.ModuleManager;

import java.util.TimerTask;
import java.util.logging.Logger;

public class AntiBotSecondTask extends TimerTask {
    private final Logger logger;
    private final AntiBot antiBot;
    private final ModuleManager moduleManager;

    public AntiBotSecondTask(final Logger logger, final AntiBot antiBot, final ModuleManager moduleManager) {
        this.logger = logger;
        this.antiBot = antiBot;
        this.moduleManager = moduleManager;
    }

    @Override
    public void run() {
        if (antiBot.isRunning()) {
            try {
                moduleManager.update();
            } catch (final Exception e) {
                logger.warning("AntiBot catched a " + e.getClass().getName() + "! (AntiBotSecondTask.java:24)");
            }
        }
    }
}
