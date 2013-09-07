package nl.thijsmolendijk.pgmtoolkit.commands;

import nl.thijsmolendijk.pgmtoolkit.PGMToolkit;
import nl.thijsmolendijk.pgmtoolkit.PlayerManager;
import nl.thijsmolendijk.pgmtoolkit.ToolkitPlayer;
import nl.thijsmolendijk.pgmtoolkit.utils.XMLParseUtils;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class GenerateCommands {
	@Command(aliases = { "cuboid", "c" }, desc = "Generate a cuboid based on the current WorldEdit selection", usage = "[name] - The name of the region", min = 1, max = 1)
	public static void cuboid(final CommandContext args, CommandSender sender) throws CommandException {
		Validate.isTrue(sender instanceof Player, "This command cannot be run from the console. Get online!");
		WorldEditPlugin we = PGMToolkit.getWorldEdit();
		ToolkitPlayer p = PlayerManager.getPlayer((Player) sender);
		Selection s = we.getSelection(p.getBukkit());
		Validate.notNull(s, "Please make an selection first!");
		Validate.isTrue(s instanceof CuboidSelection, "Non-cuboid selections are not implemented (yet).");
		CuboidSelection selection = (CuboidSelection) s;
		Vector min = selection.getNativeMinimumPoint();
        Vector max = selection.getNativeMaximumPoint();
        String str = "<cuboid name=\""+args.getJoinedStrings(0)+"\" min=\""+XMLParseUtils.weVectorToString(min)+"\" max=\""+XMLParseUtils.weVectorToString(max)+"\" />";
        p.addGeneratedStringToCache(str);
        p.getBukkit().sendMessage(ChatColor.GREEN+"Generated region. "+ChatColor.AQUA+"Use "+ChatColor.WHITE+"/toolkit generate upload"+ChatColor.AQUA+" to upload all generated regions and empty the list.");
	}
	
	@Command(aliases = { "rect", "r", "rectangle" }, desc = "Generate a rectangle based on the current WorldEdit selection", usage = "[name] - The name of the region", min = 1, max = 1)
	public static void rect(final CommandContext args, CommandSender sender) throws CommandException {
		Validate.isTrue(sender instanceof Player, "This command cannot be run from the console. Get online!");
		WorldEditPlugin we = PGMToolkit.getWorldEdit();
		ToolkitPlayer p = PlayerManager.getPlayer((Player) sender);
		Selection s = we.getSelection(p.getBukkit());
		Validate.notNull(s, "Please make an selection first!");
		Validate.isTrue(s instanceof CuboidSelection, "Non-cuboid selections are not implemented (yet).");
		CuboidSelection selection = (CuboidSelection) s;
		Vector min = selection.getNativeMinimumPoint();
        Vector max = selection.getNativeMaximumPoint();
        String str = "<rectangle name=\""+args.getJoinedStrings(0)+"\" min=\""+XMLParseUtils.we2DVectorToString(min)+"\" max=\""+XMLParseUtils.we2DVectorToString(max)+"\" />";
        p.addGeneratedStringToCache(str);
        p.getBukkit().sendMessage(ChatColor.GREEN+"Generated region. "+ChatColor.AQUA+"Use "+ChatColor.WHITE+"/toolkit generate upload"+ChatColor.AQUA+" to upload all generated regions and empty the list.");
	}
	
	@Command(aliases = { "circle", "ci" }, desc = "Generate a circle based on your current position", usage = "[radius] - The radius of the circle, [name] - The name of the region", min = 2, max = 2)
	public static void circle(final CommandContext args, CommandSender sender) throws CommandException {
		Validate.isTrue(sender instanceof Player, "This command cannot be run from the console. Get online!");
		ToolkitPlayer p = PlayerManager.getPlayer((Player) sender);
		int radius = args.getInteger(0);
		String name = args.getJoinedStrings(1);
        String str = "<circle name=\""+name+"\" center=\""+XMLParseUtils.vector2DToString(p.getBukkit().getLocation().toVector())+"\" radius=\""+radius+"\" />";
        p.addGeneratedStringToCache(str);
        p.getBukkit().sendMessage(ChatColor.GREEN+"Generated region. "+ChatColor.AQUA+"Use "+ChatColor.WHITE+"/toolkit generate upload"+ChatColor.AQUA+" to upload all generated regions and empty the list.");
	}
	
	@Command(aliases = { "cylinder", "cy" }, desc = "Generate a cylinder based on your current position", usage = "[radius] - The radius of the circle, [height] - The height of the cylinder, [name] - The name of the region", min = 3, max = 3)
	public static void cylinder(final CommandContext args, CommandSender sender) throws CommandException {
		Validate.isTrue(sender instanceof Player, "This command cannot be run from the console. Get online!");
		ToolkitPlayer p = PlayerManager.getPlayer((Player) sender);
		int radius = args.getInteger(0);
		int height = args.getInteger(1);
		String name = args.getJoinedStrings(2);
        String str = "<cylinder name=\""+name+"\" base=\""+XMLParseUtils.vectorToString(p.getBukkit().getLocation().toVector())+"\" radius=\""+radius+"\" height=\""+height+"\" />";
        p.addGeneratedStringToCache(str);
        p.getBukkit().sendMessage(ChatColor.GREEN+"Generated region. "+ChatColor.AQUA+"Use "+ChatColor.WHITE+"/toolkit generate upload"+ChatColor.AQUA+" to upload all generated regions and empty the list.");
	}
	
	@Command(aliases = { "sphere", "s" }, desc = "Generate a sphere based on your current position", usage = "[radius] - The radius of the sphere, [name] - The name of the region", min = 2, max = 2)
	public static void sphere(final CommandContext args, CommandSender sender) throws CommandException {
		Validate.isTrue(sender instanceof Player, "This command cannot be run from the console. Get online!");
		ToolkitPlayer p = PlayerManager.getPlayer((Player) sender);
		int radius = args.getInteger(0);
		String name = args.getJoinedStrings(2);
        String str = "<sphere name=\""+name+"\" origin=\""+XMLParseUtils.vectorToString(p.getBukkit().getLocation().toVector())+"\" radius=\""+radius+"\" />";
        p.addGeneratedStringToCache(str);
        p.getBukkit().sendMessage(ChatColor.GREEN+"Generated region. "+ChatColor.AQUA+"Use "+ChatColor.WHITE+"/toolkit generate upload"+ChatColor.AQUA+" to upload all generated regions and empty the list.");
	}
	
	
	@Command(aliases = { "upload", "u" }, desc = "Upload all generated regions to DPaste", min = 0, max = 0)
	public static void upload(final CommandContext args, CommandSender sender) throws CommandException {
		Validate.isTrue(sender instanceof Player, "This command cannot be run from the console. Get online!");
		ToolkitPlayer p = PlayerManager.getPlayer((Player) sender);
		p.getBukkit().sendMessage(p.uploadGeneratedStringCache());
	}
}
