package nl.thijsmolendijk.pgmtoolkit.utils;

import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;

import com.google.common.collect.Lists;

public class LocationUtils {
	public static List<Chunk> getChunks(List<Location> locations) {
		List<Chunk> result = Lists.newArrayList();
		for (Location l : locations) {
			if (!result.contains(l.getChunk()))
				result.add(l.getChunk());
		}
		return result;
	}

}
