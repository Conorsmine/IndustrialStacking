package com.conorsmine.net.industrialstacking;


import com.conorsmine.net.industrialstacking.machinestack.MachineStack;
import de.tr7zw.nbtapi.NBTTileEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class StackManager extends ConcurrentHashMap<Location, MachineStack> {

    private final IndustrialStacking pl;

    StackManager(IndustrialStacking pl) {
        this.pl = pl;
        startTicker();
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

            if (!isValidBlock(machineStack, machineLoc)) {
                this.remove(machineLoc);
                continue;
            }

            machineStack.tick();
        }
    }

    private boolean isValidBlock(MachineStack machineStack, Location machineLocation) {
        final Block block = machineLocation.getBlock();
        if (machineStack == null || machineStack.getMachineTile() == null) return false;
        if (block == null || block.getType() != machineStack.getMachineType()) return false;
        return true;
    }
}