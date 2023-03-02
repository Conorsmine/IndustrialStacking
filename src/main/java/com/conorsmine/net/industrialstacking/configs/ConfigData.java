package com.conorsmine.net.industrialstacking.configs;

import java.util.Map;

/**
 * Meant as a collection of all data within the mods config.
 */
public interface ConfigData {

    /**
     * @return Map representing the data in the config.
     */
    Map<String, Object> getConfigData();

    /**
     * @param key Key of the config value.
     * @param value Value of the config.
     */
    void addConfigData(String key, Object value);
}
