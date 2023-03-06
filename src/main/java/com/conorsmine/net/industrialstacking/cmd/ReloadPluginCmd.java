package com.conorsmine.net.industrialstacking.cmd;

import com.conorsmine.net.industrialstacking.IndustrialStacking;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ReloadPluginCmd extends Cmd {

    private static final String PERMISSION = "IndustrialStacking.reload.plugin";

    public ReloadPluginCmd(IndustrialStacking pl) {
        super(pl);
    }

    @Override
    public String getDescription() {
        return "§7Reloads the plugin config.";
    }

    @Override
    public String getUsage() {
        return "§3/is reloadPlugin";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(PERMISSION)) { nonPermissionMsg(sender, PERMISSION); return false; }

        pl.reloadConfig();
        pl.getMachineConfigFile().initConfig();

        sender.sendMessage(String.format("%s§r§7§m-----§r §eReload plugin §7§m-----§r", pl.getPrefix()));
        pl.sendConfigFileInfo(sender);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
