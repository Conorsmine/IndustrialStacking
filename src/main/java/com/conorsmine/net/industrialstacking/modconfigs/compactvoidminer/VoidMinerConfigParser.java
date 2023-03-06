package com.conorsmine.net.industrialstacking.modconfigs.compactvoidminer;

import com.conorsmine.net.industrialstacking.IndustrialStacking;
import com.conorsmine.net.industrialstacking.machinestack.StackableMachines;
import com.conorsmine.net.industrialstacking.modconfigs.ConfigParser;
import com.conorsmine.net.industrialstacking.modconfigs.DataTypes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VoidMinerConfigParser implements ConfigParser<VoidMinerConfigData> {

    private static final String FILE_NAME = "compactvoidminers.cfg";
    private static final String[] CONFIG_SECTIONS = new String[] { "general" }; // Config sections to be parsed

    private final IndustrialStacking pl;
    @SuppressWarnings("FieldMayBeFinal")
    private boolean isInstalled;
    private final Map<StackableMachines, VoidMinerConfigData> configDataMap = new HashMap<>();

    public VoidMinerConfigParser(IndustrialStacking pl) {
        this.pl = pl;
        this.isInstalled = isConfigFilePresent();
    }

    @Override
    public void parse() {
        if (!isInstalled) return;
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
        final StackableMachines machine = StackableMachines.COMPACT_VOID_MINER; // I can do this, since it's the only machine
        for (String line : lines.subList(startIndex, lines.size())) {
            if (line.matches(".*}.*")) return;    // Config section is done

            if (!DataTypes.isData(line)) continue;
            String dataKey = DataTypes.getDataKey(line);
            Object dataValue = DataTypes.getDataValue(line);

            VoidMinerConfigData configData = configDataMap.getOrDefault(machine, new VoidMinerConfigData());
            configData.addConfigData(dataKey, dataValue);
            configDataMap.put(machine, configData);
        }
    }

    private boolean isConfigSectionBeginning(String line) {
        for (String configSection : CONFIG_SECTIONS) {
            if (line.matches(String.format(".*%s.*", configSection)))
                return true;
        }

        return false;
    }

    @Override
    public Map<StackableMachines, VoidMinerConfigData> getConfigMap() {
        return configDataMap;
    }


    @Override
    public boolean isInstalled() {
        return isInstalled;
    }

    private boolean isConfigFilePresent() {
        return new File(pl.getModsConfigDir(), FILE_NAME).exists();
    }
}
