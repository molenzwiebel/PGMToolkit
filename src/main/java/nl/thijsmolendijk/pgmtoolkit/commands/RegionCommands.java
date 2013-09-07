package nl.thijsmolendijk.pgmtoolkit.commands;

import java.io.File;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import nl.thijsmolendijk.pgmtoolkit.PlayerManager;
import nl.thijsmolendijk.pgmtoolkit.ToolkitPlayer;
import nl.thijsmolendijk.pgmtoolkit.region.IRegion;
import nl.thijsmolendijk.pgmtoolkit.utils.StringUtils;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.w3c.dom.Document;

import com.sk89q.bukkit.pagination.PaginatedResult;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;

public class RegionCommands {
	@Command(aliases = { "load" }, desc = "Load an xml file", usage = "[location] - Can be either a file in the server root or an url", min = 1, max = -1)
	public static void load(CommandContext args, CommandSender sender) throws CommandException {
		Validate.isTrue(sender instanceof Player, "This command cannot be run from the console. Get online!");
		ToolkitPlayer p = PlayerManager.getPlayer((Player) sender);
		String xmlLocation = args.getJoinedStrings(0);
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = null;
			if (!xmlLocation.startsWith("http")) { 
				doc = dBuilder.parse(new File(xmlLocation));
			} else {
				doc = dBuilder.parse(xmlLocation);
			}
			p.getBukkit().sendMessage(p.loadXML(xmlLocation, doc));
		} catch (Exception e) {
			p.getBukkit().sendMessage(ChatColor.RED+"Could not parse XML. Error:");
			p.getBukkit().sendMessage(e.getMessage());
			e.printStackTrace();
		}
	}

	@Command(aliases = { "list" }, desc = "List all the loaded regions", min = 0, max = 1)
	public static void list(CommandContext args, CommandSender sender) throws CommandException {
		Validate.isTrue(sender instanceof Player, "This command cannot be run from the console. Get online!");
		ToolkitPlayer p = PlayerManager.getPlayer((Player) sender);
		Validate.isTrue(p.getRegions().size() > 0, "Please load an map using /toolkit regions load first.");
		new PaginatedResult<Entry<String, IRegion>>(8) {
			@Override public String format(Entry<String, IRegion> info, int index) {
				return ChatColor.AQUA.toString() + info.getKey() + ": " + ChatColor.GREEN + info.getValue().toString();
			}
			@Override public String formatHeader(int page, int maxPages) {
				ChatColor dashColor = ChatColor.RED;
				ChatColor textColor = ChatColor.DARK_AQUA;
				ChatColor highlight = ChatColor.AQUA;

				String message = textColor + "Loaded regions" + textColor + " (" + highlight + page + textColor + " of " + highlight + maxPages + textColor + ")";
				return StringUtils.padMessage(message, "-", dashColor, textColor);
			}
		}.display(sender, p.getRegions().entrySet(), args.getInteger(0, 1));
	}
	
	@Command(aliases = { "show" }, desc = "Show the defined region", usage = "[region] - The region needed to be shown. [-m <material id>] - Change the id air blocks are changed to", min = 1, max = -1, flags="fm:")
	public static void show(CommandContext args, CommandSender sender) throws CommandException {
		Validate.isTrue(sender instanceof Player, "This command cannot be run from the console. Get online!");
		ToolkitPlayer p = PlayerManager.getPlayer((Player) sender);
		Validate.isTrue(p.getRegions().size() > 0, "Please load an map using /toolkit regions load first.");
		if (!args.hasFlag('m'))
			p.showRegion(args.getJoinedStrings(0), args.hasFlag('f'), Material.GLASS);
		else
			p.showRegion(args.getJoinedStrings(0), args.hasFlag('f'), Material.matchMaterial(args.getFlag('m')));
		sender.sendMessage(ChatColor.GREEN+"Successfully shown region "+ChatColor.AQUA+args.getJoinedStrings(0));
	}
	
	@Command(aliases = { "hide" }, desc = "Hide the defined region", usage = "[region] - The region needed to be hidden", min = 1, max = -1)
	public static void hide(CommandContext args, CommandSender sender) throws CommandException {
		Validate.isTrue(sender instanceof Player, "This command cannot be run from the console. Get online!");
		ToolkitPlayer p = PlayerManager.getPlayer((Player) sender);
		Validate.isTrue(p.getRegions().size() > 0, "Please load an map using /toolkit regions load first.");
		p.hideRegion(args.getJoinedStrings(0));
		sender.sendMessage(ChatColor.GREEN+"Successfully hidden region "+ChatColor.AQUA+args.getJoinedStrings(0));
	}
	
	@Command(aliases = { "parse" }, desc = "Parse the provided region and add it to the loaded regions.", min = 1, max = -1)
	public static void parse(CommandContext args, CommandSender sender) throws CommandException {
		Validate.isTrue(sender instanceof Player, "This command cannot be run from the console. Get online!");
		ToolkitPlayer p = PlayerManager.getPlayer((Player) sender);
		p.addRegion(args.getJoinedStrings(0));
	}
}
