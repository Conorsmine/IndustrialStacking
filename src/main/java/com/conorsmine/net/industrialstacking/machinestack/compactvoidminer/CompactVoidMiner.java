package com.conorsmine.net.industrialstacking.machinestack.compactvoidminer;

import com.conorsmine.net.industrialstacking.IndustrialStacking;
import com.conorsmine.net.industrialstacking.machinestack.MachineStack;
import com.conorsmine.net.industrialstacking.machinestack.StackableMachines;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public class CompactVoidMiner extends MachineStack {

    public CompactVoidMiner(@NotNull IndustrialStacking plugin, @NotNull Block tileEntity) {
        super(plugin, tileEntity, StackableMachines.COMPACT_VOID_MINER);
    }

    @Override
    public void tickMachine() {
        final int energyLevel = getMachineTile().getInteger("energyLevel");
        final long requiredPower = CompactMinerUtils.getInputPowerPerMachine(getPl(), getMachineTile());

        final int supportableMachines = (int) Math.floorDiv(energyLevel, requiredPower);
        final int runningMachines = Math.min(supportableMachines, getStackAmount());
        tickwarpMiner(runningMachines);

        // Remove power
        getMachineTile().setInteger("energyLevel", (int) Math.max(energyLevel - (runningMachines * requiredPower), 0));
    }

    /**
     * <p>Decreases the remaining time of the miner.</p>
     * <p>Should ticks be greater than the remaining time on the miner, it'll clamp to 0, as to ensure that the miner adds the ore.</p>
     * @param ticks The amount of ticks to decrease by
     */
    private void tickwarpMiner(int ticks) {
        final int remainingTime = getMachineTile().getInteger("remainingTime");
        getMachineTile().setInteger("remainingTime", Math.max(remainingTime - ticks, 0));
    }
}
