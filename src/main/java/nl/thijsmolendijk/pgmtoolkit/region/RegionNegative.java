package nl.thijsmolendijk.pgmtoolkit.region;

import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

public class RegionNegative implements IRegion {
	private IRegion child;
	public RegionNegative(IRegion child) {
		this.child = child;
	}
	
	@Override
	public List<Location> blocks(World w) {
		return this.child.blocks(w);
	}

	@Override
	public String toString() {
		return "Negative region. Contains: "+this.child.toString();
	}
	
	@Override
	public List<Location> blocksInChunk(Chunk c) {
		return this.child.blocksInChunk(c);
	}
}
