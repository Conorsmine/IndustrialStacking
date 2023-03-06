package com.conorsmine.net.industrialstacking.files;

import com.conorsmine.net.industrialstacking.IndustrialStacking;
import com.conorsmine.net.industrialstacking.machinestack.StackableMachines;
import com.conorsmine.net.industrialstacking.machinestack.StackableMods;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class MachineConfigFile {

    private final IndustrialStacking pl;
    private final Map<String, Integer> maxStackSizeMap = new HashMap<>();
    private final Map<StackableMods, Integer> idOffsetMap = new HashMap<>();

    public MachineConfigFile(IndustrialStacking pl) {
        this.pl = pl;
    }

    public MachineConfigFile initConfig() {
        updateMaxStackMap();
        updateOffsetMap();
        return this;
    }

    public void updateMaxStackMap() {
        maxStackSizeMap.clear();
        for (StackableMods stackableMods : StackableMods.values()) {
            final String modKey = stackableMods.getConfigName();
            final StackableMachines[] modMachines = StackableMachines.getModMachines().get(stackableMods);
            final ConfigurationSection modSection = pl.getConfig().getConfigurationSection(modKey);

            for (StackableMachines modMachine : modMachines) {
                final String machineKey = modMachine.getConfigName();
                int maxStackSize = 0;
                if (modSection.contains(machineKey))
                    maxStackSize = modSection.getConfigurationSection(machineKey).getInt("maxStackSize", 0);

                maxStackSizeMap.put(machineKey, maxStackSize);
            }
        }
    }

    private void updateOffsetMap() {
        idOffsetMap.clear();
        for (StackableMods mod : StackableMods.values()) {
            idOffsetMap.put(mod, pl.getConfig().getConfigurationSection(mod.getConfigName()).getInt("id_offset", 0));
        }
    }


    public Map<String, Integer> getMaxStackSizeMap() {
        return maxStackSizeMap;
    }

    public Map<StackableMods, Integer> getIdOffsetMap() {
        return idOffsetMap;
    }
}
