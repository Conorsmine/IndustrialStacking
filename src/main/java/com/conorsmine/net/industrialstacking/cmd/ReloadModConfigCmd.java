package com.conorsmine.net.industrialstacking.cmd;

import com.conorsmine.net.industrialstacking.IndustrialStacking;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ReloadModConfigCmd extends Cmd {

    private static final String PERMISSION = "IndustrialStacking.reload.mod";

    public ReloadModConfigCmd(IndustrialStacking pl) {
        super(pl);
    }

    @Override
    String getDescription() {
        return "§7Reloads the mod configs.";
    }

    @Override
    String getUsage() {
        return "§3/is reloadMod";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(PERMISSION)) { nonPermissionMsg(sender, PERMISSION); return false; }

        sender.sendMessage(String.format("%s§r§7§m     §r §eReload mods §7§m     §r", pl.getPrefix()));
        pl.getModConfigManager().getForegoingConfigParser().parse();
        pl.getModConfigManager().getVoidMinerConfigParser().parse();
        pl.sendModConfigInfo(sender);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
