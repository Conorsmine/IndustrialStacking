package com.conorsmine.net.industrialstacking.cmd;

import com.conorsmine.net.industrialstacking.IndustrialStacking;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ReloadCmd extends Cmd {

    private static final String PERMISSION_ALL = "IndustrialStacking.reload.all";
    private static final String PERMISSION_Plugin = "IndustrialStacking.reload.plugin";
    private static final String PERMISSION_SAVE = "IndustrialStacking.reload.save";
    private static final String PERMISSION_MOD = "IndustrialStacking.reload.mod";

    public ReloadCmd(IndustrialStacking pl) {
        super(pl);
    }

    @Override
    public String getDescription() {
        return "§7Reloads configurations";
    }

    @Override
    public String getUsage() {
        return "§3/is reload §7[§b<all,plugin,save,mod>§7]§r";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) { reloadAll(sender); return true; }
        final String reloadSection = args[1].toLowerCase(Locale.ROOT);

        switch (reloadSection) {
            case "all":
                reloadAll(sender);
                break;
            case "plugin":
                sender.sendMessage(String.format(getCmdHeader(), "Reload plugin"));
                reloadPlugin(sender);
                break;
            case "save":
                sender.sendMessage(String.format(getCmdHeader(), "Reload save"));
                reloadSave(sender);
                break;
            case "mod":
                sender.sendMessage(String.format(getCmdHeader(), "Reload mods"));
                reloadMod(sender);
                break;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 2) return Arrays.asList("all", "plugin", "save", "mod");
        return null;
    }

    private void reloadAll(CommandSender sender) {
        if (!sender.hasPermission(PERMISSION_ALL)) { nonPermissionMsg(sender, PERMISSION_ALL); return; }
        sender.sendMessage(String.format(getCmdHeader(), "Reload"));

        reloadPlugin(sender);
        reloadSave(sender);
        reloadMod(sender);
    }

    private void reloadPlugin(CommandSender sender) {
        if (!sender.hasPermission(PERMISSION_Plugin)) { nonPermissionMsg(sender, PERMISSION_Plugin); return; }

        pl.reloadConfig();
        pl.getMachineConfigFile().initConfig();

        pl.sendConfigFileInfo(sender);
    }

    private void reloadSave(CommandSender sender) {
        if (!sender.hasPermission(PERMISSION_SAVE)) { nonPermissionMsg(sender, PERMISSION_SAVE); return; }

        pl.getMachineSaveFile().reload();
        pl.getStackManager().queueReload();
        pl.sendSaveFileInfo(sender);
    }

    private void reloadMod(CommandSender sender) {
        if (!sender.hasPermission(PERMISSION_MOD)) { nonPermissionMsg(sender, PERMISSION_MOD); return; }

        pl.getModConfigManager().getForegoingConfigParser().parse();
        pl.getModConfigManager().getVoidMinerConfigParser().parse();
        pl.sendModConfigInfo(sender);
    }
}
