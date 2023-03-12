package com.conorsmine.net.industrialstacking.machinestack.industrialforegoing.machines;

import com.conorsmine.net.industrialstacking.IndustrialStacking;
import com.conorsmine.net.industrialstacking.machinestack.MachineStack;
import com.conorsmine.net.industrialstacking.machinestack.StackableMachines;
import de.tr7zw.nbtapi.NBTCompound;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TreeFluidExtractor extends MachineStack {

    private static final Map<Integer, Byte> PROGRESS_MAP = new HashMap<>(); // Loc hash - damage byte

    private static final int TICK_RATE = 5;
    private static final double BREAK_CHANCE = 0.005d;

    private byte workTick = 0;
    private int additive = 0;   // The amount of latex to be added

    public TreeFluidExtractor(@NotNull IndustrialStacking plugin, @NotNull Block tileEntity) {
        super(plugin, tileEntity, StackableMachines.TREE_FLUID_EXTRACTOR);
    }

    @Override
    public void tickMachine() {
        workTick++;
        NBTCompound fluidTankNBT = getFluidTankNBT();
        int fluidAmount = fluidTankNBT.getInteger("Amount");
        if (fluidAmount == 8000) return;
        if (fluidAmount > 8000) { fluidTankNBT.setInteger("Amount", 8000); return; }
        if ((workTick % TICK_RATE) != 0) return;
        workTick = 0;
        final Block logBlock = getLogBlock();
        if (logBlock == null) return;

        final Random rand = new Random();
        final Location logLoc = logBlock.getLocation();
        int logHash = logLoc.hashCode();
        Byte logProgress = PROGRESS_MAP.getOrDefault(logHash, ((byte) 0));
        additive += getStackAmount();

        final double stackBreakChance = (BREAK_CHANCE * getStackAmount());
        if (!(rand.nextDouble() <= stackBreakChance)) return;       // No log break progression

        PROGRESS_MAP.put(logHash, ++logProgress);
        if (!manageLogBreaking(logBlock, logProgress)) return;    // Log wasn't broken

        // Log was broken, add all latex at once
        if (fluidTankNBT.hasTag("Empty")) { fluidTankNBT.removeKey("Empty"); fluidTankNBT.setString("FluidName", "latex"); }
        fluidTankNBT.setInteger("Amount", Math.min((fluidAmount + additive), 8000));
        additive = 0;
    }

    private NBTCompound getFluidTankNBT() {
        return getMachineTile().getCompound("fluids").getCompoundList("tanks").get(0);
    }

    // Todo:
    //  Might add an option in the config to specify different types of logs
    @Nullable
    private Block getLogBlock() {
        final Location location = getBlock().getLocation();
        final Block eastBlock = location.getWorld().getBlockAt(location.clone().add(1, 0, 0));
        final Block westBlock = location.getWorld().getBlockAt(location.clone().add(-1, 0, 0));
        final Block northBlock = location.getWorld().getBlockAt(location.clone().add(0, 0, -1));
        final Block southBlock = location.getWorld().getBlockAt(location.clone().add(0, 0, 1));

        if (eastBlock.getType() == Material.LOG || eastBlock.getType() == Material.LOG_2) return eastBlock;
        if (westBlock.getType() == Material.LOG || westBlock.getType() == Material.LOG_2) return westBlock;
        if (northBlock.getType() == Material.LOG || northBlock.getType() == Material.LOG_2) return northBlock;
        if (southBlock.getType() == Material.LOG || southBlock.getType() == Material.LOG_2) return southBlock;
        return null;
    }

    /**
     * @return True if the block was destroyed
     */
    private boolean manageLogBreaking(Block logBlock, Byte progress) {
        boolean brokeLog = (progress >= 8);
        if (brokeLog) {
            logBlock.setType(Material.AIR);
            PROGRESS_MAP.remove(logBlock.getLocation().hashCode());
            return true;
        }

        return false;
    }
}
