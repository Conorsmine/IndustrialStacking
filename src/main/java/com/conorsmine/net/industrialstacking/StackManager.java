package com.conorsmine.net.industrialstacking;


import com.conorsmine.net.industrialstacking.machinestack.MachineStack;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class StackManager extends ConcurrentHashMap<Location, MachineStack> {

    private final IndustrialStacking pl;
    private boolean shouldReload = false;

    StackManager(IndustrialStacking pl) {
        this.pl = pl;
        startTicker();
    }

    public StackManager initManager() {
        clear();
        putAll(pl.getMachineSaveFile().mapDeserializedData());
        shouldReload = false;
        return this;
    }

    public void queueReload() {
        shouldReload = true;
    }

    private void startTicker() {
        Bukkit.getScheduler().runTaskLater(pl, () -> {
            Bukkit.getScheduler().runTaskTimer(pl, this::iterateMachines, 0, 1);

            // Some initial delay
        }, 80L);
    }

    private void iterateMachines() {
        for (Entry<Location, MachineStack> mapEntries : this.entrySet()) {
            final MachineStack machineStack = mapEntries.getValue();
            final Location machineLoc = mapEntries.getKey();

            if (!machineLoc.getChunk().isLoaded()) continue;
            if (!isValidBlock(machineStack, machineLoc)) { machineStack.removeMachineStack(); continue; }
            machineStack.tick();
        }

        if (shouldReload)
            initManager();
    }

    private boolean isValidBlock(MachineStack machineStack, Location machineLocation) {
        final Block block = machineLocation.getBlock();
        if (machineStack == null || machineStack.getMachineTile() == null) return false;
        if (block == null || block.getType() != machineStack.getMachineType()) return false;
        return true;
    }
}