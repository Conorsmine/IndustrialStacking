package com.conorsmine.net.industrialstacking.files;

import com.conorsmine.net.industrialstacking.machinestack.MachineStack;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.json.simple.JSONObject;

public class MachineSaveWrapper {

    private final Location machineLocation;
    private final int machineStackSize;

    MachineSaveWrapper(final MachineStack machineStack) {
        this.machineLocation = machineStack.getBlock().getLocation();
        this.machineStackSize = machineStack.getStackAmount();
    }

    MachineSaveWrapper(final JSONObject machineJson) {
        final JSONObject locationJson = (JSONObject) machineJson.get("Location");
        this.machineLocation =  new Location(Bukkit.getWorld(
                ((String) locationJson.get("world"))),
                ((Number) locationJson.get("x")).doubleValue(),
                ((Number) locationJson.get("y")).doubleValue(),
                ((Number) locationJson.get("z")).doubleValue());
        this.machineStackSize = ((Number) machineJson.get("StackSize")).intValue();
    }

    public Location getMachineLocation() {
        return machineLocation;
    }

    public int getMachineStackSize() {
        return machineStackSize;
    }

    @Override
    public String toString() {
        return String.format("Machine:{%s, StackSize:%d}", machineLocation, machineStackSize);
    }
}
