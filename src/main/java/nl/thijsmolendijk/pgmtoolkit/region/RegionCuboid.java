package nl.thijsmolendijk.pgmtoolkit.region;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import com.google.common.collect.Lists;

public class RegionCuboid implements IRegion {
	public Vector min;
	public Vector max;
	public List<Vector> range = new ArrayList<Vector>();
	public RegionCuboid(Vector l1, Vector l2) {
		this.min = l1;
		this.max = l2;
		this.range = this.loopThrough(l1, l2);
	}
	
	
	//HELPER METHOD!!
	public List<Vector> loopThrough(Vector loc1, Vector loc2) {
		List<Vector> toReturn = new ArrayList<Vector>();
	    int minx = Math.min(loc1.getBlockX(), loc2.getBlockX()),
	    miny = Math.min(loc1.getBlockY(), loc2.getBlockY()),
	    minz = Math.min(loc1.getBlockZ(), loc2.getBlockZ()),
	    maxx = Math.max(loc1.getBlockX(), loc2.getBlockX()),
	    maxy = Math.max(loc1.getBlockY(), loc2.getBlockY()),
	    maxz = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
	    for(int x = minx; x<=maxx;x++){
	        for(int y = miny; y<=maxy;y++){
	            for(int z = minz; z<=maxz;z++){
	                Vector v = new Vector(x, y, z);
	                toReturn.add(v);
	            }
	        }
	    }
	    return toReturn;
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
		return "Cuboid region. min: "+this.min.toString()+", max: "+this.max.toString();
	}
}
