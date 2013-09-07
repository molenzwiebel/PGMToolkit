package nl.thijsmolendijk.pgmtoolkit.region;

import nl.thijsmolendijk.pgmtoolkit.utils.XMLParseUtils;

import org.bukkit.util.Vector;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RegionUtils {
	public static boolean isValidRegionTag(Node tag) {
		if (tag.getNodeType() != Node.ELEMENT_NODE)
			return false;
		Element e = (Element) tag;
		if (e.getNodeName().equals("cuboid")) {
			if (e.hasAttribute("min") && e.hasAttribute("max"))
				return true;
			return false;
		}
		if (e.getNodeName().equals("rectangle")) {
			if (e.hasAttribute("min") && e.hasAttribute("max"))
				return true;
			return false;
		}
		if (e.getNodeName().equals("cylinder")) {
			if (e.hasAttribute("base") && e.hasAttribute("radius") && e.hasAttribute("height"))
				return true;
			return false;
		}
		if (e.getNodeName().equals("circle")) {
			if (e.hasAttribute("center") && e.hasAttribute("radius"))
				return true;
			return false;
		}
		if (e.getNodeName().equals("sphere")) {
			if (e.hasAttribute("center") && e.hasAttribute("radius"))
				return true;
			return false;
		}
		if (e.getNodeName().equals("union")) {
			return true;
		}
		if (e.getNodeName().equals("complement")) {
			return true;
		}
		if (e.getNodeName().equals("negative")) {
			return true;
		}
		if (e.getNodeName().equals("point")) {
			return true;
		}
		return false;
	}
	
	public static IRegion parseRegion(Element tag) {
		if (tag.getNodeName().equals("cylinder"))
			return RegionUtils.parseCylinder(tag);
		if (tag.getNodeName().equals("cuboid"))
			return RegionUtils.parseCuboid(tag);
		if (tag.getNodeName().equals("rectangle"))
			return RegionUtils.parseRectangle(tag);
		if (tag.getNodeName().equals("circle"))
			return RegionUtils.parseCircle(tag);
		if (tag.getNodeName().equals("sphere"))
			return RegionUtils.parseSphere(tag);
		if (tag.getNodeName().equals("union"))
			return RegionUtils.parseUnion(tag);
		if (tag.getNodeName().equals("complement"))
			return RegionUtils.parseComplement(tag);
		if (tag.getNodeName().equals("negative"))
			return RegionUtils.parseNegative(tag);
		if (tag.getNodeName().equals("point"))
			return RegionUtils.parsePoint(tag);
		return null;
	}
	
	public static IRegion parseCuboid(Element tag) {
		Vector min = XMLParseUtils.parseVector(tag.getAttribute("min"));
		Vector max = XMLParseUtils.parseVector(tag.getAttribute("max"));
		return new RegionCuboid(min, max);
	}
	
	public static IRegion parseRectangle(Element tag) {
		Vector min = XMLParseUtils.parse2DVector(tag.getAttribute("min"));
		Vector max = XMLParseUtils.parse2DVector(tag.getAttribute("max"));
		return new RegionRectangle(min, max);
	}
	
	public static IRegion parseCylinder(Element tag) {
		Vector base = XMLParseUtils.parseVector(tag.getAttribute("base"));
		int radius = (int) Double.parseDouble(tag.getAttribute("radius"));
		int height = (int) Double.parseDouble(tag.getAttribute("height"));
		return new RegionCilinder(base, radius, height);
	}
	
	public static IRegion parsePoint(Element tag) {
		Vector base = XMLParseUtils.parseVector(tag.getTextContent());
		return new RegionPoint(base);
	}
	
	public static IRegion parseCircle(Element tag) {
		Vector base = XMLParseUtils.parse2DVector(tag.getAttribute("center"));
		int radius = (int) Double.parseDouble(tag.getAttribute("radius"));
		return new RegionCircle(base, radius);
	}
	
	public static IRegion parseSphere(Element tag) {
		Vector base = XMLParseUtils.parseVector(tag.getAttribute("center"));
		int radius = (int) Double.parseDouble(tag.getAttribute("radius"));
		return new RegionSphere(base, radius);
	}
	
	public static IRegion parseUnion(Element tag) {
		NodeList list = tag.getChildNodes();
		IRegion[] regionsFound = new IRegion[list.getLength()];
		for (int i = 0; i < list.getLength(); i++) {
			Node n = list.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) n;
				if (!RegionUtils.isValidRegionTag(e))
					continue;
				regionsFound[i] = RegionUtils.parseRegion(e);
			}
		}
		return new RegionUnion(regionsFound);
	}
	
	
	public static IRegion parseComplement(Element tag) {
		NodeList list = tag.getChildNodes();
		boolean firstElementFound = false;
		IRegion firstRegion = null;
		IRegion[] regions = new IRegion[list.getLength()];
		for (int i = 0; i < list.getLength(); i++) {
			Node n = list.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) n;
				if (!RegionUtils.isValidRegionTag(e))
					continue;
				if (!firstElementFound) {
					firstRegion = RegionUtils.parseRegion(e);
					firstElementFound = true;
					continue;
				}
				regions[i] = RegionUtils.parseRegion(e);
			}
		}
		return new RegionComplement(firstRegion, regions);
	}
	public static IRegion parseNegative(Element tag) {
		RegionNegative toReturn = new RegionNegative(RegionUtils.parseUnion(tag));
		return toReturn;
	}
}
