package nl.thijsmolendijk.pgmtoolkit;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import nl.thijsmolendijk.pgmtoolkit.region.IRegion;
import nl.thijsmolendijk.pgmtoolkit.region.RegionUtils;
import nl.thijsmolendijk.pgmtoolkit.utils.LocationUtils;
import nl.thijsmolendijk.pgmtoolkit.utils.PasteUtils;
import nl.thijsmolendijk.pgmtoolkit.utils.ReflectionUtil;
import nl.thijsmolendijk.pgmtoolkit.utils.RegionViewingUtils;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ToolkitPlayer {
	private String currentXML = "";
	private HashMap<String, IRegion> loadedRegions = Maps.newHashMap();
	private List<String> shownRegions = Lists.newArrayList();
	private List<String> generatedRegionsCache = Lists.newArrayList();
	private Player bukkit;

	public ToolkitPlayer(Player b) {
		this.bukkit = b;
	}

	public String loadXML(String loc, Document doc) {
		Element main = (Element) doc.getElementsByTagName("regions").item(0);
		if (main == null) return ChatColor.RESET+"No <regions> tag was found.";
		this.hideAllRegions();
		this.loadedRegions.clear();
		StringBuilder results = new StringBuilder();
		int appliedRegions = 0, normalRegions = 0;
		for (int i = 0; i < main.getChildNodes().getLength(); i++) {
			Node n = main.getChildNodes().item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) n;
				//Validate the region
				if (!RegionUtils.isValidRegionTag(e) && !e.getNodeName().equalsIgnoreCase("apply") && !e.getNodeName().equalsIgnoreCase("playable"))
					continue;

				//Placeholders
				IRegion parsedRegion = null;
				String name = "";

				//Parse apply, playable and normal regions
				if (e.getNodeName().equalsIgnoreCase("apply")) {
					parsedRegion = RegionUtils.parseUnion(e);
					appliedRegions++;
					name = "applied_region-"+appliedRegions;
				} else if (e.getNodeName().equalsIgnoreCase("playable")) {
					parsedRegion = RegionUtils.parseRegion(e);
					name = "playable";
				} else {
					parsedRegion = RegionUtils.parseRegion(e);
				}

				//Add to the hashmap
				if (!e.hasAttribute("name")) {
					if (name.equals("")) {
						normalRegions++;
						name = "region-"+normalRegions;
					}
					this.loadedRegions.put(name, parsedRegion);
				} else {
					this.loadedRegions.put(e.getAttribute("name"), parsedRegion);
				}
			}
		}
		results.append(ChatColor.GREEN+"XML parsed succesfully. "+ChatColor.AQUA.toString()+this.loadedRegions.size()+ChatColor.GREEN+" regions were loaded.");
		this.currentXML = loc;
		return results.toString();
	}

	public void showRegion(String region, boolean force, Material mat) {
		//Get and validate the region
		IRegion r = this.loadedRegions.get(region);
		Validate.notNull(r, "The specified region could not be found");

		//Make sure it is not already shown
		if (this.shownRegions.contains(region))
			Validate.notNull(null, "This region is already shown!");
		List<Location> locations = r.blocks(getBukkit().getWorld());

		//More than 20000 blocks can cause serious lag. Asking for a comfirmation
		if (locations.size() > 20000 && !force)
			Validate.notNull(null, "You are trying to show more than 20000 blocks. That may take a lot of time and resources. We have cancelled the command for you, but you can bypass this check by adding -f.");
		else {
			RegionViewingUtils.showLargerList(locations, getBukkit(), mat);
			this.shownRegions.add(region);
		}
	}

	public void hideRegion(String region) {
		//Get and validate the region
		IRegion r = this.loadedRegions.get(region);
		Validate.notNull(r, "The specified region could not be found");

		if (!this.shownRegions.contains(region))
			Validate.notNull(null, "This region is already hidden!");
		List<Chunk> chunks = LocationUtils.getChunks(r.blocks(getBukkit().getWorld()));

		//Resend all the chunks. Undoing the block changes sent.
		for (Chunk c : chunks)
			ReflectionUtil.resendChunkToPlayer(c, getBukkit());

		this.shownRegions.remove(region);

		//Resend the blocks that have been removed by the chunk refresh
		for (Chunk c : chunks)
			for (String key : this.shownRegions) {
				IRegion reg = this.loadedRegions.get(key);
				if (reg.blocksInChunk(c).size() > 1)
					RegionViewingUtils.showLargerList(reg.blocksInChunk(c), getBukkit(), Material.GLASS);
			}
	}

	public void hideAllRegions() {
		List<Chunk> chunks = Lists.newArrayList();
		for (String key : this.shownRegions) {
			IRegion reg = this.loadedRegions.get(key);
			chunks.addAll(LocationUtils.getChunks(reg.blocks(getBukkit().getWorld())));
		}
		for (Chunk c : chunks)
			ReflectionUtil.resendChunkToPlayer(c, getBukkit());
		this.shownRegions.clear();
	}

	public void hideAllRegionsInChunk(Chunk c) {
		if (this.shownRegions.size() == 0)
			return;
		List<String> regionsToRemove = Lists.newArrayList();
		for (String key : this.shownRegions) {
			IRegion reg = this.loadedRegions.get(key);
			List<Chunk> chunks = LocationUtils.getChunks(reg.blocks(getBukkit().getWorld()));
			if (chunks.contains(c)) {
				getBukkit().sendMessage(ChatColor.GREEN+"[PGMToolkit] "+ChatColor.RED+"Region "+ChatColor.AQUA+key+ChatColor.RED+" was hidden because the chunk it is in was unloaded.");
				regionsToRemove.add(key);
			}
		}
		for (String str : regionsToRemove)
			this.hideRegion(str);
	}

	public void addRegion(String regionXML) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new InputSource(new StringReader("<regions>"+regionXML+"</regions>")));
			Element e = (Element) doc.getDocumentElement().getFirstChild();
			IRegion r = RegionUtils.parseRegion(e);
			if (e.hasAttribute("name"))
				this.loadedRegions.put(e.getAttribute("name"), r);
			else
				this.loadedRegions.put("region-"+this.loadedRegions.size(), r);
		} catch (Exception e) {
			Validate.isTrue(false, "Could not parse xml. Error:\n"+e.getMessage());
		}
	}

	public void addGeneratedStringToCache(String str) {
		this.generatedRegionsCache.add(str+"\n\n");
	}

	public String uploadGeneratedStringCache() {
		Validate.isTrue(this.generatedRegionsCache.size() > 0, "Please generate some regions first.");
		try {
			String link = PasteUtils.uploadToDPaste(Joiner.on("").join(this.generatedRegionsCache));
			this.generatedRegionsCache.clear();
			return ChatColor.GREEN+"Upload successful. "+ChatColor.WHITE+"Click on this link to view the XML: "+link;
		} catch (Exception e) {
			e.printStackTrace();
			return ChatColor.RED+"Something went wrong trying to upload the xml to DPaste. Please try again.";
		}
	}

	public HashMap<String, IRegion> getRegions() {
		return this.loadedRegions;
	}

	public String getCurrentXML() {
		return currentXML;
	}

	public IRegion getRegion(String key) {
		return this.loadedRegions.get(key);
	}

	public Player getBukkit() {
		return bukkit;
	}

	public boolean isViewingRegions() {
		return this.shownRegions.size() > 0;
	}

}
