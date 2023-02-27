package com.conorsmine.net.industrialstacking.files;

import com.conorsmine.net.industrialstacking.IndustrialStacking;
import com.conorsmine.net.industrialstacking.machinestack.MachineStack;
import com.conorsmine.net.industrialstacking.machinestack.StackableMachines;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unchecked")
public class MachineSaveFile {

    private final IndustrialStacking pl;
    private static final String fileName = "machines.json";
    private File logFile;
    private JSONObject jsonFile;

    public MachineSaveFile(IndustrialStacking pl) {
        this.pl = pl;
        this.logFile = createLogFile();
        this.jsonFile = parseFile(logFile);
    }

    private File createLogFile() {
        File file = new File(pl.getDataFolder().getAbsolutePath() + File.separator + fileName);
        if (file.exists()) return file;

        try {
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write("{}");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            pl.getLogger().info("§cCould not create log file at \"" + pl.getDataFolder().getAbsolutePath() + File.pathSeparator + fileName + "\"!");
        }

        return file;
    }

    private JSONObject parseFile(File file) {
        JSONParser parser = new JSONParser();
        FileReader reader = null;
        try {
            reader = new FileReader(file);
            return (JSONObject) parser.parse(reader);

        } catch (ParseException | IOException e) {
            e.printStackTrace();
            pl.getLogger().info("§cCould not parse log file!\nPlease make sure that the §7" + fileName + "§c file is valid JSON!");
        }

        return null;
    }

    private void save() {
        try {
            FileWriter writer = new FileWriter(logFile);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(jsonFile));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return Map of the JSON file
     */
    public Map<Material, Set<MachineSaveWrapper>> deserialize() {
        final Map<Material, Set<MachineSaveWrapper>> machineMap = new HashMap<>();

        for (Object o : jsonFile.keySet()) {
            final Material material = Material.matchMaterial(((String) o));
            if (material == null) continue;

            final Set<MachineSaveWrapper> machines = new HashSet<>();
            final JSONArray machineTypeArr = (JSONArray) jsonFile.get(o);

            for (Object o1 : machineTypeArr)
                machines.add(new MachineSaveWrapper(((JSONObject) o1)));

            machineMap.put(material, machines);
        }
        return machineMap;
    }

    /**
     * @return Map structured after {@link com.conorsmine.net.industrialstacking.StackManager}
     */
    public Map<Location, MachineStack> mapDeserializedData() {
        final Map<Location, MachineStack> map = new HashMap<>();
        for (Map.Entry<Material, Set<MachineSaveWrapper>> materialSetEntry : deserialize().entrySet()) {
            final Material material = materialSetEntry.getKey();
            final Set<MachineSaveWrapper> machineSaveWrappers = materialSetEntry.getValue();

            for (MachineSaveWrapper saveWrapper : machineSaveWrappers) {
                final Location machineLocation = saveWrapper.getMachineLocation();

                final MachineStack machineStack = getMachineStack(machineLocation, material, saveWrapper.getMachineStackSize());
                if (machineStack == null) continue;
                map.put(machineLocation, machineStack);
            }
        }

        return map;
    }

    private MachineStack getMachineStack(final Location location, final Material material, final int stackSize) {
        final Block machineBlock = location.getBlock();
        if (machineBlock == null || !machineBlock.getType().name().equals(material.name())) return null;
        final StackableMachines stackableMachinesEnum = StackableMachines.machineFromName(material.name());
        if (stackableMachinesEnum == null) return null;
        final MachineStack machineStack = stackableMachinesEnum.createNew(machineBlock);
        if (machineStack == null) return null;
        machineStack.setMachineStackAmount(stackSize);
        return machineStack;
    }

    public void reload() {
        this.logFile = createLogFile();
        this.jsonFile = parseFile(logFile);
    }

    public void saveMachineStack(final MachineStack machineStack) {
        final Location location = machineStack.getBlock().getLocation();
        final String machineTypeName = machineStack.getMachineType().name();
        final JSONArray machineTypeArr = (JSONArray) jsonFile.getOrDefault(machineTypeName, new JSONArray());
        final JSONObject machineJson = new JSONObject();
        final int machineIndex = getSameMachineIndex(location, machineTypeArr);

        machineJson.put("Location", createLocationJson(location));
        machineJson.put("StackSize", machineStack.getStackAmount());
        if (machineIndex == machineTypeArr.size())
            machineTypeArr.add(machineJson);
        else
            machineTypeArr.set(machineIndex, machineJson);
        jsonFile.put(machineTypeName, machineTypeArr);
        save();
    }

    public void removeMachineStack(final MachineStack machineStack) {
        final String key = machineStack.getMachineType().name();
        final JSONArray machineTypeArr = (JSONArray) jsonFile.get(key);
        final int machineIndex = getSameMachineIndex(machineStack.getBlock().getLocation(), machineTypeArr);
        System.out.println(machineIndex);
        machineTypeArr.remove(machineIndex);
        jsonFile.put(key, machineTypeArr);
        save();
    }

    /**
     * Tries to find the saved JSON for that particular machine.
     * @param location The location of the machine stack.
     * @param machineTypeArr The saved JSON array for that type of machine.
     * @return Index of the machineTypeArr it's located at.
     */
    private int getSameMachineIndex(final Location location, final JSONArray machineTypeArr) {
        for (int i = 0; i < machineTypeArr.size(); i++) {
            final JSONObject machineJson = ((JSONObject) machineTypeArr.get(i));
            final JSONObject machineLocation = (JSONObject) machineJson.get("Location");
            if (isSameLocation(location, machineLocation)) return i;
        }

        return machineTypeArr.size();
    }

    private boolean isSameLocation(final Location location, final JSONObject locationJson) {
        final Location machineLocation = new Location(Bukkit.getWorld(
                ((String) locationJson.get("world"))),
                ((Number) locationJson.get("x")).doubleValue(),
                ((Number) locationJson.get("y")).doubleValue(),
                ((Number) locationJson.get("z")).doubleValue());
        return location.equals(machineLocation);
    }

    private JSONObject createLocationJson(final Location location) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("world", location.getWorld().getName());
        jsonObject.put("x", location.getBlockX());
        jsonObject.put("y", location.getBlockY());
        jsonObject.put("z", location.getBlockZ());

        return jsonObject;
    }
}
