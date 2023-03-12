package com.conorsmine.net.industrialstacking.machinestack.industrialforegoing.machines;

import com.conorsmine.net.industrialstacking.IndustrialStacking;
import com.conorsmine.net.industrialstacking.machinestack.MachineStack;
import com.conorsmine.net.industrialstacking.machinestack.Powerable;
import com.conorsmine.net.industrialstacking.machinestack.StackableMachines;
import com.conorsmine.net.industrialstacking.machinestack.industrialforegoing.ForegoingUtils;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public class MobDuplicator extends MachineStack implements Powerable {
    public MobDuplicator(@NotNull IndustrialStacking plugin, @NotNull Block tileEntity) {
        super(plugin, tileEntity, StackableMachines.MOB_DUPLICATOR);
    }

    @Override
    public void tickMachine() {
        if (getMachineTile().getLong("TeslaPower") == 0L || getMachineTile().getBoolean("paused")) return;
        getMachineTile().getCompound("work_energy").setLong("TeslaInput", getMachineStackPower());
    }

    @Override
    public long getRegularMachinePower() {
        return ForegoingUtils.getInputPowerForMachine(this);
    }

    @Override
    public long getMachineStackPower() {
        return getRegularMachinePower() * (getStackAmount() + 1);
    }
}
