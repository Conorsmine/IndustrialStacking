package com.conorsmine.net.industrialstacking.modconfigs.industrialforegoing;

import com.conorsmine.net.industrialstacking.IndustrialStacking;
import com.conorsmine.net.industrialstacking.modconfigs.ConfigParser;
import com.conorsmine.net.industrialstacking.machinestack.StackableMachines;
import com.conorsmine.net.industrialstacking.modconfigs.DataTypes;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ForegoingConfigParser implements ConfigParser<ForegoingConfigData> {

    private static final String FILE_NAME = "industrialforegoing.cfg";
    private static final StackableMachines[] CONFIG_MACHINES = new StackableMachines[]
            { StackableMachines.LASER_DRILL, StackableMachines.LASER_BASE, StackableMachines.HYDRATOR,
                    StackableMachines.VILLAGER_TRADE_EXCHANGER, StackableMachines.RESOURCEFUL_FURNACE,
                    StackableMachines.MATERIAL_STONEWORK_FACTORY, StackableMachines.ANIMAL_SEWER,
                    StackableMachines.TREE_FLUID_EXTRACTOR, StackableMachines.MOB_DUPLICATOR,
                    StackableMachines.RESOURCE_FISHER, StackableMachines.POTION_BREWER };

    private final IndustrialStacking pl;
    private final Map<StackableMachines, ForegoingConfigData> configDataMap = new HashMap<>();

    public ForegoingConfigParser(IndustrialStacking pl) {
        this.pl = pl;
    }


    @Override
    public void parse() {
        final File foregoingConfig = new File(pl.getModsConfigDir(), FILE_NAME);

        try {
            BufferedReader reader = new BufferedReader(new FileReader(foregoingConfig));
            parseConfigData(reader.lines().collect(Collectors.toList()));

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseConfigData(List<String> lines) {
        for (int i = 0; i < lines.size(); i++) {
            if (isConfigSectionBeginning(lines.get(i)))
                parseConfigSection(lines, i);
        }
    }

    private void parseConfigSection(List<String> lines, int startIndex) {
        final StackableMachines machine = getMachine(lines.get(startIndex));
        for (String line : lines.subList(startIndex, lines.size())) {
            if (line.matches(".*}.*")) return;    // Config section is done

            if (!DataTypes.isData(line)) continue;
            String dataKey = DataTypes.getDataKey(line);
            Object dataValue = DataTypes.getDataValue(line);

            ForegoingConfigData configData = configDataMap.getOrDefault(machine, new ForegoingConfigData());
            configData.addConfigData(dataKey, dataValue);
            configDataMap.put(machine, configData);
        }
    }

    private boolean isConfigSectionBeginning(String line) {
        for (StackableMachines machine : CONFIG_MACHINES) {
            if (line.matches(String.format(".*%s.*", machine.getConfigName())))
                return true;
        }

        return false;
    }

    private StackableMachines getMachine(String sectionHead) {
        for (StackableMachines configMachine : CONFIG_MACHINES) {
            if (sectionHead.contains(configMachine.getConfigName()))
                return configMachine;
        }

        // Should never happen
        return null;
    }

    @Override
    public Map<StackableMachines, ForegoingConfigData> getConfigMap() {
        return configDataMap;
    }

    @Override
    public StackableMachines[] getConfigMachines() {
        return CONFIG_MACHINES;
    }
}
