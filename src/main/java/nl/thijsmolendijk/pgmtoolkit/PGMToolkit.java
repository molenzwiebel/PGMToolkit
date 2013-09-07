package nl.thijsmolendijk.pgmtoolkit;

import nl.thijsmolendijk.pgmtoolkit.commands.MainCommand.MainParentCommand;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissionsException;
import com.sk89q.minecraft.util.commands.CommandUsageException;
import com.sk89q.minecraft.util.commands.CommandsManager;
import com.sk89q.minecraft.util.commands.MissingNestedCommandException;
import com.sk89q.minecraft.util.commands.WrappedCommandException;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class PGMToolkit extends JavaPlugin {
    private CommandsManager<CommandSender> commands;

    @Override
    public void onEnable() {
	this.setupCommands();
	this.saveDefaultConfig();
	this.getServer().getPluginManager().registerEvents(new PlayerManager(), this);
	if (this.getConfig().getBoolean("enable_chunk_listener")) this.getServer().getPluginManager().registerEvents(new ToolkitListener(), this);
    }

    @Override
    public void onDisable() {
	for (ToolkitPlayer p : PlayerManager.getPlayers()) {
	    if (p.isViewingRegions()) {
		p.getBukkit().sendMessage(ChatColor.GREEN + "[PGMToolkit] " + ChatColor.RED + "All regions were hidden because of a reload.");
		p.hideAllRegions();
	    }
	}
    }

    private void setupCommands() {
	this.commands = new CommandsManager<CommandSender>() {
	    @Override
	    public boolean hasPermission(CommandSender sender, String perm) {
		return sender instanceof ConsoleCommandSender || sender.hasPermission(perm);
	    }
	};
	CommandsManagerRegistration cmdRegister = new CommandsManagerRegistration(this, this.commands);
	cmdRegister.register(MainParentCommand.class);
    }

    public static WorldEditPlugin getWorldEdit() {
	WorldEditPlugin worldEditPlugin = null;
	worldEditPlugin = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
	if (worldEditPlugin == null) {
	    Validate.notNull(null, "WorldEdit is null. Please install WorldEdit if you want to make use of its features.");
	}
	return worldEditPlugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String commandLabel, String[] args) {
	try {
	    this.commands.execute(cmd.getName(), args, sender, sender);
	}
	catch (CommandPermissionsException e) {
	    sender.sendMessage(ChatColor.RED + "You don't have permission.");
	}
	catch (MissingNestedCommandException e) {
	    sender.sendMessage(ChatColor.RED + e.getUsage());
	}
	catch (CommandUsageException e) {
	    sender.sendMessage(ChatColor.RED + e.getMessage());
	    sender.sendMessage(ChatColor.RED + e.getUsage());
	}
	catch (WrappedCommandException e) {
	    if (e.getCause() instanceof NumberFormatException) {
		sender.sendMessage(ChatColor.RED + "Number expected, string received instead.");
	    }
	    else if (e.getCause() instanceof IllegalArgumentException) {
		sender.sendMessage(ChatColor.RED + e.getMessage().replace("java.lang.IllegalArgumentException: ", ""));
	    }
	    else {
		sender.sendMessage(ChatColor.RED + "An error has occurred. See console.");
		e.printStackTrace();
	    }
	}
	catch (CommandException e) {
	    sender.sendMessage(ChatColor.RED + e.getMessage());
	}
	return true;
    }
}
