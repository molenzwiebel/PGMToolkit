package nl.thijsmolendijk.pgmtoolkit.region;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import com.google.common.collect.Lists;

public class RegionPoint implements IRegion {
	private Vector block;

	public RegionPoint(Vector block) {
		this.block = block;
	}

	@Override
	public List<Location> blocks(World w) {
		return Arrays.asList(this.block.toLocation(w));
	}

	@Override
	public String toString() {
		return "Point region. Location: "+this.block.toString();
	}

	@Override
	public List<Location> blocksInChunk(Chunk c) {
		List<Location> returning = Lists.newArrayList();
		if (block.toLocation(c.getWorld()).getChunk().equals(c))
			returning.add(block.toLocation(c.getWorld()));
		return returning;
	}
}
