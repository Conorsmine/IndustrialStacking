package com.conorsmine.net.industrialstacking;

import com.conorsmine.net.industrialstacking.cmd.ReloadCmd;
import com.conorsmine.net.industrialstacking.files.MachineConfigFile;
import com.conorsmine.net.industrialstacking.files.MachineSaveFile;
import com.conorsmine.net.industrialstacking.files.MachineSaveWrapper;
import com.conorsmine.net.industrialstacking.machinestack.StackableMachines;
import com.conorsmine.net.industrialstacking.machinestack.StackableMods;
import com.conorsmine.net.industrialstacking.modconfigs.ModConfigManager;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public final class IndustrialStacking extends JavaPlugin {

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
        sendInfoDataMsg(getServer().getConsoleSender());
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

    public void sendInfoDataMsg(final CommandSender sender) {
        sender.sendMessage(String.format("%s§r§7§m     §r %s §7§m     §r", getPrefix(), getPrefix()));
        sender.sendMessage(String.format("%s§aEnabling %s§7-§6%s§r", getPrefix(), getPrefix(), getDescription().getVersion()));

        // Send config file info
        sender.sendMessage(getPrefix());
        sender.sendMessage(String.format("%s§aLoading §6plugin config:§r", getPrefix()));
        Iterator<StackableMods> configIterator = machineConfigFile.getIdOffsetMap().keySet().iterator();
        while (configIterator.hasNext()) {
            StackableMods mods = configIterator.next();
            sender.sendMessage(String.format("%s§e%s §7machine configs:§r", getPrefix(), mods.name()));
            sender.sendMessage(String.format("%s §3id offset: §b%s§r", getPrefix(), machineConfigFile.getIdOffsetMap().getOrDefault(mods, 0)));
            sender.sendMessage(String.format("%s §3max stack sizes:§r", getPrefix()));

            for (StackableMachines machines : StackableMachines.getModMachines().get(mods)) {
                int maxStackSize = machineConfigFile.getMaxStackSizeMap().getOrDefault(machines.getConfigName(), 0);
                sender.sendMessage(String.format("%s  §7>> §3%s: §b%d§r", getPrefix(), machines.name(), maxStackSize));
            }

            if (configIterator.hasNext()) sender.sendMessage(getPrefix());
        }


        // Send save file info
        sender.sendMessage(getPrefix());
        sender.sendMessage(String.format("%s§aLoading §6save file:§r", getPrefix()));
        int total = 0;
        for (Map.Entry<Material, List<MachineSaveWrapper>> entry : machineSaveFile.deserialize().entrySet()) {
            sender.sendMessage(String.format("%s  §7>> §3%s: §b%d §7instances§r", getPrefix(), StackableMachines.machineFromName(entry.getKey().name()), entry.getValue().size()));
            total += entry.getValue().size();
        }
        sender.sendMessage(String.format("%s§7Loaded a total of §b%d §7stacked machines.§7", getPrefix(), total));

        // Send mod config info
        boolean ifLoaded = modConfigManager.getForegoingConfigParser().isInstalled();
        boolean minerLoaded = modConfigManager.getVoidMinerConfigParser().isInstalled();

        sender.sendMessage(getPrefix());
        sender.sendMessage(String.format("%s§aLoading §6mod configs:§r", getPrefix()));
        sender.sendMessage(String.format("%s§eIndustrialForegoing §7is §r%s§7.§r", getPrefix(),
                ((ifLoaded) ? "§ainstalled" : "§cmissing")));
        if (ifLoaded) {
            sender.sendMessage(String.format("%s§eThe following machine configs have been found:§r", getPrefix()));

            for (StackableMachines ifMachine : modConfigManager.getForegoingConfig().keySet()) {
                sender.sendMessage(String.format("%s §7>> §3%s", getPrefix(), ifMachine.name()));
            }
            sender.sendMessage(getPrefix());
        }

        sender.sendMessage(String.format("%s§eCompactVoidMiners §7is §r%s§7.§r", getPrefix(),
                ((minerLoaded) ? "§ainstalled" : "§cmissing")));
        if (minerLoaded) {
            sender.sendMessage(String.format("%s§eThe following machine configs have been found:§r", getPrefix()));

            for (StackableMachines minerMachine : modConfigManager.getVoidMinerConfig().keySet()) {
                sender.sendMessage(String.format("%s §7>> §3%s", getPrefix(), minerMachine.name()));
            }
        }
    }

    public File getModsConfigDir() {
        final File rootFile = getServer().getWorldContainer().getParentFile();
        return new File(rootFile, "config");
    }

    public String getPrefix() {
        return "§c§l[§8Industrial§6Stacking§c§l]§r ";
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
