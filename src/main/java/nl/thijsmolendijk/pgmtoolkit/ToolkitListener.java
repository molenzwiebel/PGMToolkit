package nl.thijsmolendijk.pgmtoolkit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

public class ToolkitListener implements Listener {
	
	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent e) {
		for (ToolkitPlayer p : PlayerManager.getPlayers())
			p.hideAllRegionsInChunk(e.getChunk());
	}
	
}
