package com.conorsmine.net.industrialstacking.cmd;

import com.conorsmine.net.industrialstacking.IndustrialStacking;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class InfoCmd extends Cmd {

    private static final String PERMISSION = "IndIndustrialStacking.info";

    public InfoCmd(IndustrialStacking pl) {
        super(pl);
    }

    @Override
    public String getDescription() {
        return "§7View the inner workings of the plugin";
    }

    @Override
    public String getUsage() {
        return "§3/is info §7[§b<plugin,save,mod>§7]";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(PERMISSION)) { nonPermissionMsg(sender, PERMISSION); return false; }

        if (args.length >= 2 && args[1].toLowerCase(Locale.ROOT).equals("plugin"))      {
            sender.sendMessage(String.format(getCmdHeader(), "Info-Plugin"));
            pl.runConfigFileInfo(sender);
        }
        else if (args.length >= 2 && args[1].toLowerCase(Locale.ROOT).equals("save"))   {
            sender.sendMessage(String.format(getCmdHeader(), "Info-Save"));
            pl.runSaveFileInfo(sender);
        }
        else if (args.length >= 2 && args[1].toLowerCase(Locale.ROOT).equals("mod"))    {
            sender.sendMessage(String.format(getCmdHeader(), "Info-Mod"));
            pl.runModConfigInfo(sender);
        }
        else {
            sender.sendMessage(String.format(getCmdHeader(), "Info"));
            pl.runConfigFileInfo(sender);
            sender.sendMessage(pl.getPrefix());
            pl.runSaveFileInfo(sender);
            sender.sendMessage(pl.getPrefix());
            pl.runModConfigInfo(sender);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 2) return Arrays.asList("plugin", "save", "mod");
        return null;
    }
}
