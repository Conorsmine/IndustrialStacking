package com.conorsmine.net.industrialstacking;

import com.conorsmine.net.industrialstacking.cmd.ReloadCmd;
import com.conorsmine.net.industrialstacking.files.MachineConfigFile;
import com.conorsmine.net.industrialstacking.files.MachineSaveFile;
import com.conorsmine.net.industrialstacking.files.MachineSaveWrapper;
import com.conorsmine.net.industrialstacking.machinestack.MachineStack;
import com.conorsmine.net.industrialstacking.machinestack.StackableMachines;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
public final class IndustrialStacking extends JavaPlugin {

    private final String prefix = "§c§l[§eIndustrialStacking§c]§r ";
    private StackManager stackManager;
    private MachineConfigFile machineConfigFile;
    private MachineSaveFile machineSaveFile;

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
}
