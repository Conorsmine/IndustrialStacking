package com.conorsmine.net.industrialstacking.cmd;

import com.conorsmine.net.industrialstacking.IndustrialStacking;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCmd implements CommandExecutor {

    private final IndustrialStacking pl;

    public ReloadCmd(IndustrialStacking pl) {
        this.pl = pl;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("IndustrialStacking.reload")) { nonPermissionMsg(sender); return false; }

        pl.reloadConfig();
        pl.getMachineConfigFile().initConfig();
        sender.sendMessage(String.format("%s §aReloaded config!§r", pl.getPrefix()));
        return true;
    }

    private void nonPermissionMsg(CommandSender sender) {
        sender.sendMessage(String.format("%s §cYou do not have permission to run that command!§r", pl.getPrefix()));
    }
}
