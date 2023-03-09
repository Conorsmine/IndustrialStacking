package com.conorsmine.net.industrialstacking.machinestack;

/**
 * Enum representing the available mods
 */
public enum StackableMods {

    INDUSTRIAL_FOREGOING("industrial_foregoing", "INDUSTRIALFOREGOING"),
    COMPACT_VOID_MINER("compact_void_miner", "COMPACTVOIDMINERS");

    private final String configName;
    private final String modPrefix;

    StackableMods(String configName, String modPrefix) {
        this.configName = configName;
        this.modPrefix = modPrefix;
    }

    public String getConfigName() {
        return configName;
    }

    public String getModPrefix() {
        return modPrefix;
    }
}
