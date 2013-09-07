package nl.thijsmolendijk.pgmtoolkit.region;


import java.util.ArrayList;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import com.google.common.collect.Lists;


public class RegionRectangle implements IRegion {
	public Vector min;
	public Vector max;
	public List<Vector> range = new ArrayList<Vector>();

	public RegionRectangle(Vector l1, Vector l2) {
		this.min = l1;
		this.max = l2;
	}


	//HELPER METHOD!!
	public List<Vector> loopThroughAndGetHighestBlock(Vector loc1, Vector loc2, World w) {
		List<Vector> toReturn = new ArrayList<Vector>();
		int minx = Math.min(loc1.getBlockX(), loc2.getBlockX()),
				minz = Math.min(loc1.getBlockZ(), loc2.getBlockZ()),
				maxx = Math.max(loc1.getBlockX(), loc2.getBlockX()),
				maxz = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
		int highest = 0;
		for(int x = minx; x<=maxx;x++){
			for(int z = minz; z<=maxz;z++){
				int y = w.getHighestBlockYAt(new Location(w, x, 256, z));
				if (y > highest)
					highest = y;
			}
		}
		System.out.println("Highest y: "+highest);

		for(int x = minx; x<=maxx;x++){
			for(int y = 0; y<=highest;y++){
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
		List<Vector> locs = Lists.newArrayList();
		locs.addAll(this.range);
		locs.addAll(loopThroughAndGetHighestBlock(this.min, this.max, w));
		for (Vector v : locs)
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
		return "Rectangle region. Min: "+this.min.toString()+", max" +this.max.toString();
	}
}
