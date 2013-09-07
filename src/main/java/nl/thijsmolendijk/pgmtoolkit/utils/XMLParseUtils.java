package nl.thijsmolendijk.pgmtoolkit.utils;

import java.awt.Color;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.util.Vector;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XMLParseUtils {
	public static Color hex2Rgb(String colorStr) {
		return new Color(
				Integer.valueOf( colorStr.substring( 1, 3 ), 16 ),
				Integer.valueOf( colorStr.substring( 3, 5 ), 16 ),
				Integer.valueOf( colorStr.substring( 5, 7 ), 16 ) );
	}

	private static ItemStack applyColor(ItemStack item, String color) {
		ItemMeta m = item.getItemMeta();
		if (m instanceof LeatherArmorMeta) {
			LeatherArmorMeta lm = (LeatherArmorMeta) m;
			Color javaColor = hex2Rgb("#" + color);
			lm.setColor(org.bukkit.Color.fromRGB(javaColor.getRed(), javaColor.getGreen(), javaColor.getBlue()));
			item.setItemMeta(lm);
		}
		return item;
	}

	public static ItemStack parseItem(Node nodeTag) {
		Element tag = (Element) nodeTag;
		ItemStack toReturn = new ItemStack(Material.AIR);
		String materialName = tag.getTextContent().replace(" ", "_").toUpperCase();
		Material mat = Material.matchMaterial(materialName);
		toReturn.setType(mat);

		if (tag.hasAttribute("amount"))
			toReturn.setAmount(Integer.parseInt(tag.getAttribute("amount")));
		else
			toReturn.setAmount(1);

		if (tag.hasAttribute("damage"))
			toReturn.setDurability((short)Integer.parseInt(tag.getAttribute("damage")));
		else
			toReturn.setDurability((short)0);

		ItemMeta m = toReturn.getItemMeta();
		if (tag.hasAttribute("name"))
			m.setDisplayName(ChatColor.translateAlternateColorCodes('`', tag.getAttribute("name")));
		if (tag.hasAttribute("lore"))
			m.setLore(Arrays.asList(ChatColor.translateAlternateColorCodes('`', tag.getAttribute("name")).split("\\|")));
		toReturn.setItemMeta(m);
		if (tag.hasAttribute("enchantment")) {
			for (int i = 0; i < tag.getAttribute("enchantment").split(";").length; i++) {
				String rawEnch = tag.getAttribute("enchantment").split(";")[i];
				String enchantment = rawEnch.split(":")[0].replace(" ", "_").toUpperCase();
				String level = rawEnch.split(":")[1];
				Enchantment e = Enchantment.getByName(enchantment);
				toReturn.addUnsafeEnchantment(e, Integer.parseInt(level));
			}
		}

		if (tag.hasAttribute("color"))
			toReturn = applyColor(toReturn, tag.getAttribute("color"));
		return toReturn;
	}
	
	public static Vector parseVector(String vec) {
		return new Vector(Double.parseDouble(vec.split(",")[0]), Double.parseDouble(vec.split(",")[1]), Double.parseDouble(vec.split(",")[2]));
	}
	
	public static Vector parse2DVector(String vec) {
		return new Vector(Double.parseDouble(vec.split(",")[0]), 0, Double.parseDouble(vec.split(",")[1]));
	}
	
	public static String vectorToString(Vector vec) {
		return vec.getBlockX()+","+vec.getBlockY()+","+vec.getBlockZ();
	}

	public static String vector2DToString(Vector vec) {
		return vec.getBlockX()+","+vec.getBlockZ();
	}
	
	public static String weVectorToString(com.sk89q.worldedit.Vector vec) {
		return vec.getX()+","+vec.getY()+","+vec.getZ();
	}
	
	public static String we2DVectorToString(com.sk89q.worldedit.Vector vec) {
		return vec.getX()+","+vec.getZ();
	}
}
