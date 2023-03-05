package com.conorsmine.net.industrialstacking.cmd;

import com.conorsmine.net.industrialstacking.IndustrialStacking;
import com.conorsmine.net.industrialstacking.StackManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ReloadAllCmd extends Cmd {

    private static final String PERMISSION = "IndustrialStacking.reload.all";

    public ReloadAllCmd(IndustrialStacking pl) {
        super(pl);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(PERMISSION)) { nonPermissionMsg(sender, PERMISSION); return false; }

        // Reload pl
        pl.saveDefaultConfig();
        pl.getMachineConfigFile().initConfig();

        // Reload mod
        pl.getModConfigManager().getForegoingConfigParser().parse();
        pl.getModConfigManager().getVoidMinerConfigParser().parse();

        // Reload save
        pl.getMachineSaveFile().reload();
        pl.getStackManager().addToActionQueue(() -> {
            pl.getStackManager().clear();
            pl.getStackManager().putAll(pl.getMachineSaveFile().mapDeserializedData());
        });

        sender.sendMessage(String.format("%s§r§7§m     §r §eReload all §7§m     §r", pl.getPrefix()));
        pl.sendConfigFileInfo(sender);
        pl.sendSaveFileInfo(sender);
        pl.sendModConfigInfo(sender);
        return true;
    }

    @Override
    public String getDescription() {
        return "§7Reloads the whole plugin";
    }

    @Override
    String getUsage() {
        return "§3/is reloadAll";
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
