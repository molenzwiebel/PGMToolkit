package nl.thijsmolendijk.pgmtoolkit.region;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import com.google.common.collect.Lists;

public class RegionCircle implements IRegion {
	private List<Vector> range = new ArrayList<Vector>();
	public Vector base;
	private int radius;
	public RegionCircle(Vector center, int radius) {
		this.radius = radius;
		this.base = center;
	}

	private List<Vector> circle(Vector loc, int r, World w) {
		List<Vector> data = new ArrayList<Vector>();
		int cx = loc.getBlockX();
		int cz = loc.getBlockZ();
		int rSquared = r * r;
		int highestY = 0;
		for (int x = cx - r; x <= cx +r; x++) {
			for (int z = cz - r; z <= cz +r; z++) {
				if ((cx - x) * (cx -x) + (cz - z) * (cz - z) <= rSquared) {
					int y = w.getHighestBlockYAt(x, z);
					if (y > highestY)
						highestY = y;
				}
			}
		}

		for (int x = cx - r; x <= cx +r; x++) {
			for (int y = 0; y < highestY; y++) {
				for (int z = cz - r; z <= cz +r; z++) {
					if ((cx - x) * (cx -x) + (cz - z) * (cz - z) <= rSquared) {
						data.add(new Vector(x, y, z));
					}
				}
			}
		}
		return data;
	}

	@Override
	public List<Location> blocks(World w) {
		List<Location> returning = Lists.newArrayList();
		List<Vector> locs = Lists.newArrayList();
		locs.addAll(this.range);
		locs.addAll(circle(this.base, this.radius, w));
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
		return "Circle region. Base: "+this.base.toString()+", radius: "+this.radius;
	}
}
