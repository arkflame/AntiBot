package twolovers.antibot.bungee.tasks;

import java.util.logging.Logger;

import twolovers.antibot.bungee.AntiBot;
import twolovers.antibot.bungee.module.ModuleManager;

public class AntiBotSecondTask implements Runnable {
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
        while (antiBot.isRunning()) {
            try {
                moduleManager.update();
                Thread.sleep(1000);
            } catch (final Exception e) {
                logger.warning("AntiBot catched a " + e.getClass().getName() + "! (ModuleManager.java:44)");
            }
        }
    }
}
