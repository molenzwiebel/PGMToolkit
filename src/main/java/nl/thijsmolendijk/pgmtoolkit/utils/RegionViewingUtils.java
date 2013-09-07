package nl.thijsmolendijk.pgmtoolkit.utils;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Lists;

public class RegionViewingUtils {
	public static void displayList(List<Location> list, Player p, Material m) {
		for (Location l : list) {
			if (l.getBlock().getType() == Material.AIR)
				p.sendBlockChange(l, m, (byte) 0);
		}
	}

	public static void showLargerList(List<Location> list, final Player p, final Material m) {
		int noLists = (int) list.size() / 40;
		if (noLists < 1)
			return;
		List<List<Location>> subLists = Lists.partition(list, noLists);
		for (final List<Location> sublist : subLists)
			new BukkitRunnable() {
				@Override
				public void run() {
					displayList(sublist, p, m);
				}
			}.runTaskLater(Bukkit.getPluginManager().getPlugin("PGMToolkit"), 30L);
	}
}
