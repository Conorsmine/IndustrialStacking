package com.conorsmine.net.industrialstacking.modconfigs;

import com.conorsmine.net.industrialstacking.IndustrialStacking;
import com.conorsmine.net.industrialstacking.machinestack.StackableMachines;
import com.conorsmine.net.industrialstacking.modconfigs.industrialforegoing.ForegoingConfigData;
import com.conorsmine.net.industrialstacking.modconfigs.industrialforegoing.ForegoingConfigParser;

import java.util.Map;

/**
 * Class meant to encapsulate and maintain all {@link ConfigParser}
 */
public class ModConfigManager {

    private final IndustrialStacking pl;

    private final ForegoingConfigParser foregoingConfigParser;

    public ModConfigManager(IndustrialStacking pl) {
        this.pl = pl;

        this.foregoingConfigParser = new ForegoingConfigParser(pl);
        this.foregoingConfigParser.parse();
    }

    public Map<StackableMachines, ForegoingConfigData> getForegoingConfig() {
        return foregoingConfigParser.getConfigMap();
    }
}
