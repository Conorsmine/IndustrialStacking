package com.conorsmine.net.industrialstacking;


import com.conorsmine.net.industrialstacking.machinestack.MachineStack;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class StackManager extends ConcurrentHashMap<Location, MachineStack> {

    private final IndustrialStacking pl;
    private final Queue<Runnable> afterWork = new ConcurrentLinkedQueue<>(); // A queue for actions to be performed after the next iteration of machine

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

            if (!isValidBlock(machineStack, machineLoc)) { machineStack.removeMachineStack(); continue; }
            machineStack.tick();
        }

        if (afterWork.size() > 0) {
            afterWork.element().run();
            afterWork.remove();
        }
    }

    private boolean isValidBlock(MachineStack machineStack, Location machineLocation) {
        final Block block = machineLocation.getBlock();
        if (machineStack == null || machineStack.getMachineTile() == null) return false;
        if (block == null || block.getType() != machineStack.getMachineType()) return false;
        return true;
    }

    public void addToActionQueue(Runnable action) {
        afterWork.add(action);
    }
}