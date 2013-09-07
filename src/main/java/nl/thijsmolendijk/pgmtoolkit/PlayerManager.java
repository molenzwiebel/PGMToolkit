package nl.thijsmolendijk.pgmtoolkit;

import java.util.Collection;
import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.google.common.collect.Maps;

public class PlayerManager implements Listener {
	private static HashMap<Player, ToolkitPlayer> players = Maps.newHashMap();
	
	public static ToolkitPlayer getPlayer(Player p) {
		if (players.containsKey(p))
			return players.get(p);
		ToolkitPlayer pl = new ToolkitPlayer(p);
		players.put(p, pl);
		return pl;
	}
	
	@EventHandler
	public void onPlayerLogoff(PlayerQuitEvent e) {
		players.remove(e.getPlayer());
	}

	public static Collection<ToolkitPlayer> getPlayers() {
		return players.values();
	}
}
