package nl.thijsmolendijk.pgmtoolkit.region;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import com.google.common.collect.Lists;

public class RegionComplement implements IRegion {
	public List<Vector> range = new ArrayList<Vector>();
	
	public RegionComplement(IRegion start, IRegion[] remove) {
		this.range = this.locationListToVectorList(start.blocks(Bukkit.getWorlds().get(0)));
		for (IRegion reg : remove) 
			if (reg != null)
				this.range.removeAll(this.locationListToVectorList(reg.blocks(Bukkit.getWorlds().get(0))));
	}

	@Override
	public List<Location> blocks(World w) {
		List<Location> returning = Lists.newArrayList();
		for (Vector v : this.range)
			returning.add(v.toLocation(w));
		return returning;
	}
	
	@Override
	public List<Location> blocksInChunk(Chunk c) {
		List<Location> returning = Lists.newArrayList();
		for (Vector v : this.range)
			if (v.toLocation(c.getWorld()).getChunk().equals(c))
				returning.add(v.toLocation(c.getWorld()));
		return returning;
	}
	
	@Override
	public String toString() {
		return "Complement region";
	}
	
	private List<Vector> locationListToVectorList(List<Location> list) {
		List<Vector> ret = Lists.newArrayList();
		for (Location l : list)
			ret.add(l.toVector());
		return ret;
	}

}
