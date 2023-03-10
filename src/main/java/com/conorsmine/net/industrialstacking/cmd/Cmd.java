package com.conorsmine.net.industrialstacking.cmd;

import com.conorsmine.net.industrialstacking.IndustrialStacking;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

public abstract class Cmd implements TabExecutor {

    final IndustrialStacking pl;

    public Cmd(IndustrialStacking pl) {
        this.pl = pl;
    }

    public abstract String getDescription();

    public abstract String getUsage();

    public String getCmdHeader() {
        return pl.getPrefix() + "§r§7§m-----§r §e%s §7§m-----§r";
    }

    public void nonPermissionMsg(CommandSender sender, String permission) {
        sender.sendMessage(String.format("%s§cYou do not have permission to run that command!§r", pl.getPrefix()));
        sender.sendMessage(String.format("%s§7Required permission node: §3%s§7.", pl.getPrefix(), permission));
    }
}
