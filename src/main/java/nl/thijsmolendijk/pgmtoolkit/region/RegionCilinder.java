package nl.thijsmolendijk.pgmtoolkit.region;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import com.google.common.collect.Lists;

public class RegionCilinder implements IRegion {
	private List<Vector> range = new ArrayList<Vector>();
	public Vector base;
	private int radius;
	private int height;
	public RegionCilinder(Vector center, int radius, int height) {
		this.cylinder(center, radius, height);
		this.radius = radius;
		this.height = height;
		this.base = center;
	}

	private void cylinder(Vector loc, int r, int h) {
		int cx = loc.getBlockX();
		int cy = loc.getBlockY();
		int cz = loc.getBlockZ();
		int rSquared = r * r;

		for (int x = cx - r; x <= cx +r; x++) {
			for (int y = cy; y <= cy +h; y++) {
				for (int z = cz - r; z <= cz +r; z++) {
					if ((cx - x) * (cx -x) + (cz - z) * (cz - z) <= rSquared) {
						range.add(new Vector(x, y, z));
					}
				}
			}
		}
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
		return "Cylinder region. Base: "+this.base.toString()+", radius: "+this.radius+", height: "+this.height;
	}
}
