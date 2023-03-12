package com.conorsmine.net.industrialstacking.cmd;

import com.conorsmine.net.industrialstacking.IndustrialStacking;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class HiddenTeleportCmd extends Cmd {

    private static final String PERMISSION = ProfilerCmd.PERMISSION;

    public HiddenTeleportCmd(IndustrialStacking pl) {
        super(pl);
    }

    @Override
    public String getDescription() {
        return "§7Used for text click events";
    }

    @Override
    public String getUsage() {
        return "§3/is hidtp";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(PERMISSION)) { nonPermissionMsg(sender, PERMISSION); return false; }
        if (args.length < 5) return false;
        if (!(sender instanceof Player)) return false;

        ((Player) sender).teleport(new Location(Bukkit.getWorld(UUID.fromString(args[1])), Integer.parseInt(args[2]), Integer.parseInt(args[3]) + 1.5, Integer.parseInt(args[4])));
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
