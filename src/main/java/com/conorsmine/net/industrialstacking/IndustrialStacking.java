package com.conorsmine.net.industrialstacking;

import com.conorsmine.net.industrialstacking.cmd.ReloadCmd;
import com.conorsmine.net.industrialstacking.files.MachineConfigFile;
import com.conorsmine.net.industrialstacking.files.MachineSaveFile;
import com.conorsmine.net.industrialstacking.modconfigs.ModConfigManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@SuppressWarnings("unused")
public final class IndustrialStacking extends JavaPlugin {

    private final String prefix = "§c§l[§eIndustrialStacking§c]§r ";
    private StackManager stackManager;
    private MachineConfigFile machineConfigFile;
    private MachineSaveFile machineSaveFile;
    private ModConfigManager modConfigManager;

    @Override
    public void onEnable() {
        initConfig();
        stackManager = new StackManager(this);
        stackManager.putAll(machineSaveFile.mapDeserializedData());
        getServer().getPluginManager().registerEvents(new EvenListener(this), this);
        getCommand("reloadStackables").setExecutor(new ReloadCmd(this));
    }

    @Override
    public void onDisable() {
    }

    private void initConfig() {
        this.saveDefaultConfig();
        machineConfigFile = new MachineConfigFile(this).initConfig();
        machineSaveFile = new MachineSaveFile(this);
        modConfigManager = new ModConfigManager(this);
    }

    public File getModsConfigDir() {
        final File rootFile = getServer().getWorldContainer().getParentFile();
        return new File(rootFile, "config");
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

    public MachineSaveFile getMachineSaveFile() {
        return machineSaveFile;
    }

    public ModConfigManager getModConfigManager() {
        return modConfigManager;
    }
}
