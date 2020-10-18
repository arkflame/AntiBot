package twolovers.antibot.bungee.listeners;

import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PlayerHandshakeEvent;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import twolovers.antibot.bungee.instanceables.BotPlayer;
import twolovers.antibot.bungee.module.ModuleManager;
import twolovers.antibot.bungee.module.PlayerModule;

public class PlayerHandshakeListener implements Listener {
	private final ModuleManager moduleManager;
	private final PlayerModule playerModule;

	public PlayerHandshakeListener(final ModuleManager moduleManager) {
		this.moduleManager = moduleManager;
		this.playerModule = moduleManager.getPlayerModule();
	}

	@EventHandler(priority = Byte.MIN_VALUE)
	public void onPlayerHandshake(final PlayerHandshakeEvent event) {
		if (event instanceof Cancellable && ((Cancellable) event).isCancelled()) {
			return;
		}

		final PendingConnection connection = event.getConnection();
		final String ip = connection.getAddress().getHostString();
		final BotPlayer botPlayer = playerModule.get(ip);
		final int requestedProtocol = event.getHandshake().getRequestedProtocol();
		int currentPps = moduleManager.getCurrentPps();
		int currentCps = moduleManager.getCurrentCps();

		moduleManager.addIncoming();

		if (requestedProtocol == 1) {
			currentPps++;
			botPlayer.setPPS(botPlayer.getPPS() + 1);
			moduleManager.setCurrentPPS(currentPps);
			botPlayer.setRepings(botPlayer.getRepings() + 1);
		} else {
			currentCps++;
			botPlayer.setCPS(botPlayer.getCPS() + 1);
			moduleManager.setCurrentCps(currentCps);
		}
	}
}
