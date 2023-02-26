package com.conorsmine.net.industrialstacking;

import com.conorsmine.net.industrialstacking.cmd.ReloadCmd;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public final class IndustrialStacking extends JavaPlugin {

    private final String prefix = "§c§l[§eIndustrialStacking§c]§r ";
    private StackManager stackManager;
    private MachineConfigFile machineConfigFile;

    @Override
    public void onEnable() {
        initConfig();
        machineConfigFile = new MachineConfigFile(this).initConfig();
        stackManager = new StackManager(this);
        getServer().getPluginManager().registerEvents(new EvenListener(this), this);
        getCommand("reloadStackables").setExecutor(new ReloadCmd(this));
    }

    @Override
    public void onDisable() {
    }

    private void initConfig() {
        this.saveDefaultConfig();
    }

    public String getPrefix() {
        return prefix;
    }

    public StackManager getStackManager() {
        return stackManager;
    }

    public MachineConfigFile getMachineConfigFile() {
        return machineConfigFile;
    }
}
