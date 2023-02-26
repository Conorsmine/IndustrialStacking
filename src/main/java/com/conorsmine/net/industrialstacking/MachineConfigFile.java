package com.conorsmine.net.industrialstacking;

import com.conorsmine.net.industrialstacking.machinestack.StackableMachines;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MachineConfigFile {

    private final IndustrialStacking pl;
    private final Map<String, Integer> maxStackSizeMap = new HashMap<>();

    MachineConfigFile(IndustrialStacking pl) {
        this.pl = pl;
    }

    public MachineConfigFile initConfig() {
        updateMaxStackMap();
        return this;
    }

    private void updateMaxStackMap() {
        maxStackSizeMap.clear();
        final Set<String> configKeySet = pl.getConfig().getKeys(false);
        for (StackableMachines stackableMachines : StackableMachines.values()) {
            final String key = stackableMachines.getConfigName();
            int maxStackSize = 0;
            if (configKeySet.contains(key))
                maxStackSize = pl.getConfig().getConfigurationSection(key).getInt("maxStackSize");

            maxStackSizeMap.put(key, maxStackSize);
        }
    }


    public Map<String, Integer> getMaxStackSizeMap() {
        return maxStackSizeMap;
    }
}
