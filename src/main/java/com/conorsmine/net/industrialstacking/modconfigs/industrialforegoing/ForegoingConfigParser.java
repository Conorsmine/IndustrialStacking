package com.conorsmine.net.industrialstacking.modconfigs.industrialforegoing;

import com.conorsmine.net.industrialstacking.IndustrialStacking;
import com.conorsmine.net.industrialstacking.modconfigs.ConfigParser;
import com.conorsmine.net.industrialstacking.machinestack.StackableMachines;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ForegoingConfigParser implements ConfigParser<ForegoingConfigData> {

    private static final String FILE_NAME = "industrialforegoing.cfg";
    private static final StackableMachines[] CONFIG_MACHINES = new StackableMachines[]
            {StackableMachines.LASER_DRILL, StackableMachines.LASER_BASE, StackableMachines.MOB_DUPLICATOR};

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
            pl.getLogger().info("§cMAP:");
            parseConfigData(reader.lines().collect(Collectors.toList()));
            pl.getLogger().info("§c" + configDataMap.toString());

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

        System.out.println(configDataMap);
    }

    private void parseConfigSection(List<String> lines, int startIndex) {
        final StackableMachines machine = getMachine(lines.get(startIndex));
        for (String line : lines.subList(startIndex, lines.size())) {
            System.out.println(line);
            if (line.matches(".*}.*")) return;    // Config section is done

            if (!isData(line)) continue;
            String dataKey = getDataKey(line);
            Object dataValue = getDataValue(line, getDataType(line));

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

    private boolean isData(String line) {
        return line.matches("\\s*[I|B]:.*");
    }

    private DataTypes getDataType(String line) {
        if (line.matches("\\s*I:.*")) return DataTypes.INT;
        else return DataTypes.BOOL;
    }

    private Object getDataValue(String line, DataTypes dataType) {
        final String value = line.replaceAll("\\s", "").replaceAll(".*=", "");
        if (dataType == DataTypes.BOOL)
            return Boolean.valueOf(value);
        else
            return Integer.parseInt(value);
    }

    private String getDataKey(String line) {
        return line.replaceAll("\\s", "").replaceAll("[I|B]:", "").replaceAll("=.*", "");
    }

    @Override
    public Map<StackableMachines, ForegoingConfigData> getConfigMap() {
        return configDataMap;
    }

    @Override
    public StackableMachines[] getConfigMachines() {
        return CONFIG_MACHINES;
    }


    private enum DataTypes {
        INT,
        BOOL;
    }
}
