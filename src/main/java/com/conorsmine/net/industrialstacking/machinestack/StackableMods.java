package com.conorsmine.net.industrialstacking.machinestack;

/**
 * Enum representing the available mods
 */
public enum StackableMods {

    INDUSTRIAL_FOREGOING("industrial_foregoing"),
    COMPACT_VOID_MINER("compact_void_miner");

    private final String configName;

    StackableMods(String configName) {
        this.configName = configName;
    }

    public String getConfigName() {
        return configName;
    }
}
