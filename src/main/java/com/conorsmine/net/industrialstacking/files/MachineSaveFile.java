package com.conorsmine.net.industrialstacking.files;

import com.conorsmine.net.industrialstacking.IndustrialStacking;
import com.conorsmine.net.industrialstacking.machinestack.MachineStack;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
