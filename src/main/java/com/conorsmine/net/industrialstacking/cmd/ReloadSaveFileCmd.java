package com.conorsmine.net.industrialstacking.cmd;

import com.conorsmine.net.industrialstacking.IndustrialStacking;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ReloadSaveFileCmd extends Cmd {

    private static final String PERMISSION = "IndustrialStacking.reload.save";

    public ReloadSaveFileCmd(IndustrialStacking pl) {
        super(pl);
    }

    @Override
    public String getDescription() {
        return "§7Reloads the save file.";
    }

    @Override
    public String getUsage() {
        return "§3/is reloadSave";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(PERMISSION)) { nonPermissionMsg(sender, PERMISSION); return false; }

        sender.sendMessage(String.format("%s§r§7§m-----§r §eReload save §7§m-----§r", pl.getPrefix()));
        pl.getMachineSaveFile().reload();
        pl.getStackManager().addToActionQueue(() -> {
            pl.getStackManager().clear();
            pl.getStackManager().putAll(pl.getMachineSaveFile().mapDeserializedData());
        });
        pl.sendSaveFileInfo(sender);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
