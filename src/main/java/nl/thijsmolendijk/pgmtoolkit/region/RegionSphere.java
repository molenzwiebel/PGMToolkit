package nl.thijsmolendijk.pgmtoolkit.region;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import com.google.common.collect.Lists;

public class RegionSphere implements IRegion {
	private List<Vector> range = new ArrayList<Vector>();
	public Vector base;
	private int radius;
	public RegionSphere(Vector center, int radius) {
		this.sphere(center, radius);
		this.base = center;
		this.radius = radius;
	}

	private void sphere(Vector loc, int r) {
		int bsize = r;
		int bx = loc.getBlockX();
		int by = loc.getBlockY();
		int bz = loc.getBlockZ();
		double zpow;
		double xpow;
		double bpow = Math.pow(r, 2);
		for (int z = -bsize; z <= bsize; z++) {
			zpow = Math.pow(z, 2);
			for (int x = -bsize; x <= bsize; x++) {
				xpow = Math.pow(x, 2);
				for (int y = -bsize; y <= bsize; y++) {
					if ((xpow + Math.pow(y, 2) + zpow) <= bpow) {
						range.remove(new Vector(bx + x, by + y, bz + z));
						range.add(new Vector(bx + x, by + y, bz + z));
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
		return "Sphere region. Base: "+this.base.toString()+", radius: "+this.radius;
	}
}
