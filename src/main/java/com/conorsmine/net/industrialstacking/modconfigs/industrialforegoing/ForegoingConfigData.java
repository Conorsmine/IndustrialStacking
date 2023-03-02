package com.conorsmine.net.industrialstacking.modconfigs.industrialforegoing;

import com.conorsmine.net.industrialstacking.modconfigs.ConfigData;

import java.util.HashMap;
import java.util.Map;

public class ForegoingConfigData implements ConfigData {

    public final Map<String, Object> configMap = new HashMap<>();

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
        return "ForegoingConfigData{" +
                "configMap=" + configMap +
                '}';
    }
}
