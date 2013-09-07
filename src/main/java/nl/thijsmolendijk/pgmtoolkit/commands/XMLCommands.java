package nl.thijsmolendijk.pgmtoolkit.commands;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import nl.thijsmolendijk.pgmtoolkit.utils.PasteUtils;
import nl.thijsmolendijk.pgmtoolkit.utils.XMLInventoryUtils;
import nl.thijsmolendijk.pgmtoolkit.utils.XMLParseUtils;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;

public class XMLCommands {
	@Command(aliases = { "inventory" }, desc = "Generate xml for your current inventory", min = 0, max = 0)
	public static void inventory(CommandContext args, CommandSender sender) throws CommandException {
		Validate.isTrue(sender instanceof Player, "Consoles don't have an inventory, silly!");
		Player player = (Player) sender;
		try {
			String link = PasteUtils.uploadToDPaste(XMLInventoryUtils.inventoryToXML(player));
			player.sendMessage(ChatColor.GREEN+"Upload successful. "+ChatColor.WHITE+"Click on this link to view the XML: "+link);
		} catch (Exception e) {
			player.sendMessage(ChatColor.RED+"Something went wrong trying to upload the xml to DPaste. Please try again.");
			e.printStackTrace();
		}
	}

	@Command(aliases = { "item" }, desc = "Create an item tag for the item you are currently holding", min = 0, max = 0)
	public static void item(CommandContext args, CommandSender sender) throws CommandException {
		Validate.isTrue(sender instanceof Player, "Consoles don't have an inventory, silly!");
		Player player = (Player) sender;
		Validate.isTrue(player.getItemInHand() != null, "Please hold an item in your hand");
		try {
			String link = PasteUtils.uploadToDPaste(XMLInventoryUtils.itemToXML(player.getItemInHand(), -1, "item"));
			player.sendMessage(ChatColor.GREEN+"Upload successful. "+ChatColor.WHITE+"Click on this link to view the XML: "+link);
		} catch (Exception e) {
			player.sendMessage(ChatColor.RED+"Something went wrong trying to upload the xml to DPaste. Please try again.");
			e.printStackTrace();
		}
	}

	@Command(aliases = { "parseitem" }, desc = "Creates an item from xml", usage="[xml] - the xml to create an item from", min = 1, max = -1)
	public static void itemload(CommandContext args, CommandSender sender) throws CommandException {
		Validate.isTrue(sender instanceof Player, "Consoles don't have an inventory, silly!");
		String xml = "<data>"+args.getJoinedStrings(0)+"</data>";
		Player p = (Player) sender;
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new InputSource(new StringReader(xml)));
			p.getInventory().addItem(XMLParseUtils.parseItem(doc.getDocumentElement().getFirstChild()));
		} catch (Exception e) {
			p.sendMessage(ChatColor.RED+"Could not parse XML. Error:");
			p.sendMessage(e.getMessage());
			e.printStackTrace();
		}
	}
}
