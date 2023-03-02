package com.conorsmine.net.industrialstacking.modconfigs;

import com.conorsmine.net.industrialstacking.machinestack.StackableMachines;

import java.util.Map;

/**
 * Meant as a manager class, both parsing and retaining the parsed values.
 * @param <T> The specific {@link ConfigData} to be used for returning data.
 */
public interface ConfigParser<T extends ConfigData> {

    /**
     * Parse the config file to match the return type of {@link #getConfigMap()}.
     */
    void parse();

    /**
     * @return Map of machines and their {@link ConfigData}.
     */
    Map<StackableMachines, T> getConfigMap();

    /**
     * @return Array of machines in that config.
     */
    StackableMachines[] getConfigMachines();
}
