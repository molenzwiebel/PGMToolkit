package nl.thijsmolendijk.pgmtoolkit.region;

import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

public interface IRegion {
	public List<Location> blocks(World w);
	public List<Location> blocksInChunk(Chunk c);
}
