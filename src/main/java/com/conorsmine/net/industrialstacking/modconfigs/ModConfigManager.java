package com.conorsmine.net.industrialstacking.modconfigs;

import com.conorsmine.net.industrialstacking.IndustrialStacking;
import com.conorsmine.net.industrialstacking.machinestack.StackableMachines;
import com.conorsmine.net.industrialstacking.modconfigs.compactvoidminer.VoidMinerConfigData;
import com.conorsmine.net.industrialstacking.modconfigs.compactvoidminer.VoidMinerConfigParser;
import com.conorsmine.net.industrialstacking.modconfigs.industrialforegoing.ForegoingConfigData;
import com.conorsmine.net.industrialstacking.modconfigs.industrialforegoing.ForegoingConfigParser;

import java.util.Map;

/**
 * Class meant to encapsulate and maintain all {@link ConfigParser}
 */
public class ModConfigManager {

    private final IndustrialStacking pl;

    private final ForegoingConfigParser foregoingConfigParser;
    private final VoidMinerConfigParser voidMinerConfigParser;

    public ModConfigManager(IndustrialStacking pl) {
        this.pl = pl;

        this.foregoingConfigParser = new ForegoingConfigParser(pl);
        this.foregoingConfigParser.parse();
        this.voidMinerConfigParser = new VoidMinerConfigParser(pl);
        this.voidMinerConfigParser.parse();
    }

    public Map<StackableMachines, ForegoingConfigData> getForegoingConfig() {
        return foregoingConfigParser.getConfigMap();
    }

    public Map<StackableMachines, VoidMinerConfigData> getVoidMinerConfig() {
        return voidMinerConfigParser.getConfigMap();
    }

    public ForegoingConfigParser getForegoingConfigParser() {
        return foregoingConfigParser;
    }

    public VoidMinerConfigParser getVoidMinerConfigParser() {
        return voidMinerConfigParser;
    }
}
