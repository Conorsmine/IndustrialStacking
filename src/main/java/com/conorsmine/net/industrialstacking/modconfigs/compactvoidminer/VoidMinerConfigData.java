package com.conorsmine.net.industrialstacking.modconfigs.compactvoidminer;

import com.conorsmine.net.industrialstacking.modconfigs.ConfigData;

import java.util.HashMap;
import java.util.Map;

public class VoidMinerConfigData implements ConfigData {

    private final Map<String, Object> configMap = new HashMap<>();

    @Override
    public Map<String, Object> getConfigData() {
        return configMap;
    }

    @Override
    public void addConfigData(String key, Object value) {
        configMap.put(key, value);
    }

    @Override
    public String toString() {
        return "VoidMinerConfigData{" +
                "configMap=" + configMap +
                '}';
    }
}
