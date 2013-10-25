package nl.thijsmolendijk.pgmtoolkit.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;

public class XMLInventoryUtils {
    public static String inventoryToXML(Player p) {
        HashMap<Integer, ItemStack> items = inventoryToHashMap(p);
        StringBuilder builder = new StringBuilder(itemsToXML(items));
        ItemStack[] contents = p.getInventory().getArmorContents();
        if (contents[0] != null && contents[0].getType() != Material.AIR)
            builder.append("\n").append(itemToXML(contents[0], -1, "boots"));
        if (contents[1] != null && contents[1].getType() != Material.AIR)
            builder.append("\n").append(itemToXML(contents[1], -1, "leggings"));
        if (contents[2] != null && contents[2].getType() != Material.AIR)
            builder.append("\n").append(itemToXML(contents[2], -1, "chestplate"));
        if (contents[3] != null && contents[3].getType() != Material.AIR)
            builder.append("\n").append(itemToXML(contents[3], -1, "helmet"));
        return builder.toString();
    }

    public static HashMap<Integer, ItemStack> inventoryToHashMap(Player p) {
        //Make a hashmap with the data of the inventory
        HashMap<Integer, ItemStack> map = Maps.newHashMap();
        //Method for receiving the slot of a object. A inventory has 36 slots
        for (int slot = 0; slot < 36; slot++) {
            for (ItemStack i : p.getInventory().getContents()) {
                if (p.getInventory().getItem(slot) == null || i == null) continue;
                if (p.getInventory().getItem(slot).getType() == Material.AIR || i.getType() == Material.AIR) continue;
                if (i.equals(p.getInventory().getItem(slot))) {
                    if (!map.containsKey(Integer.valueOf(slot))) {
                        map.put(slot, i);
                    }
                }
            }
        }
        return map;
    }

    //Function to turn a hashmap into xml
    public static String itemsToXML(HashMap<Integer, ItemStack> items) {
        //The lines in the xml
        ArrayList<String> str = new ArrayList<String>();

        //Explanation
        str.add("The slots are as following: \n \n Hotbar: 0-8 \n Row 1: 9-17 \n Row 2: 18-26 \n Row 3: 27 - 35 \n Armor (bottom to top) 36-39 \n\n\n");

        //Loop through every item, and generate an xml line
        for (Entry<Integer, ItemStack> entry : items.entrySet()) {
            ItemStack value = entry.getValue();
            if (value != null && value.getType() != Material.AIR) {
                str.add(itemToXML(value, entry.getKey(), "item"));
            }
        }
        return Joiner.on("\n").join(str);
    }

    public static String itemToXML(ItemStack stack, Integer slot, String tagName) {
        int amount = stack.getAmount();
        int damage = stack.getDurability();
        StringBuilder builder = new StringBuilder("<"+tagName+" ").append("amount=\""+amount+"\" ");
        if (damage > 0)
            builder.append("damage=\""+damage+"\" ");

        //ENCHANTMENTS
        if (stack.hasItemMeta() && stack.getItemMeta().hasEnchants())
            builder.append("enchantment=\"");
        for (Entry<Enchantment, Integer> enchant : stack.getEnchantments().entrySet()) {
            builder.append(enchant.getKey().getName().toString().replace("_", " ").toLowerCase()+":"+enchant.getValue()+";");
        }
        if (builder.toString().charAt(builder.length()-1) == ';') {
            //Remove the last comma
            builder = new StringBuilder(builder.substring(0, builder.length()-1));
            //Add a closing "
            builder.append("\" ");
        }
        //END ENCHANTMENTS

        //NAME AND LORE
        ItemMeta meta = stack.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            builder.append("name=\""+meta.getDisplayName().replace("??", "`")+"\" ");
        }
        if (meta != null && meta.hasLore()) {
            builder.append("lore=\""+Joiner.on("|").join(meta.getLore()).replace("??", "`")+"\" ");
        }
        //END NAME AND LORE

        //LEATHER COLOR
        if (stack.getType().toString().startsWith("LEATHER_")) {
            LeatherArmorMeta leatherMeta = (LeatherArmorMeta) stack.getItemMeta();
            String hex = String.format("%02x%02x%02x", leatherMeta.getColor().getRed(), leatherMeta.getColor().getGreen(), leatherMeta.getColor().getBlue());
            if (hex.length() < 6) {
                for (int i = 0; i < (6 - hex.length()); i++)
                    hex = "0" + hex;
            }
            builder.append("color=\""+hex+"\" ");
        }
        //END LEATHER COLOR

        //SLOT
        if (slot != -1)
            builder.append("slot=\""+slot+"\" ");
        //END SLOT

        builder = new StringBuilder(builder.toString().trim());
        builder.append(">");

        builder.append(stack.getType().toString().replace("_", " ").toLowerCase());
        builder.append("</"+tagName+">");
        return builder.toString();
    }

}
