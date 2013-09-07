package nl.thijsmolendijk.pgmtoolkit.region;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class RegionUnion implements IRegion {
	public IRegion[] regions;

	public RegionUnion(IRegion[] regions) {
		this.regions = regions;
	}

	@Override
	public List<Location> blocks(World w) {
		List<Location> returning = Lists.newArrayList();
		for (IRegion r : this.regions)
			if (r != null)
				returning.addAll(r.blocks(w));
		return returning;
	}

	@Override
	public List<Location> blocksInChunk(Chunk c) {
		List<Location> returning = Lists.newArrayList();
		for (IRegion r : this.regions)
			if (r != null)
				for (Location loc : r.blocks(c.getWorld()))
					if (loc.getChunk().equals(c))
						returning.add(loc);
		return returning;
	}

	@Override
	public String toString() {
		List<IRegion> notEmptyRegions = Lists.newArrayList();
		for (IRegion r : this.regions)
			if (r != null)
				notEmptyRegions.add(r);
		return "Union region. "+ChatColor.WHITE+"Regions: "+Joiner.on(ChatColor.GREEN+", "+ChatColor.WHITE).join(notEmptyRegions);
	}

}
