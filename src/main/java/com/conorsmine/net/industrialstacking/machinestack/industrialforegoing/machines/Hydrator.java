package com.conorsmine.net.industrialstacking.machinestack.industrialforegoing.machines;

import com.conorsmine.net.industrialstacking.IndustrialStacking;
import com.conorsmine.net.industrialstacking.machinestack.MachineStack;
import com.conorsmine.net.industrialstacking.machinestack.Powerable;
import com.conorsmine.net.industrialstacking.machinestack.StackableMachines;
import com.conorsmine.net.industrialstacking.machinestack.industrialforegoing.ForegoingUtils;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public class Hydrator extends MachineStack implements Powerable {
    public Hydrator(@NotNull IndustrialStacking plugin, @NotNull Block tileEntity) {
        super(plugin, tileEntity, StackableMachines.HYDRATOR);
    }

    @Override
    public long getRegularMachinePower() {
        return ForegoingUtils.getInputPowerForMachine(this);
    }

    @Override
    public long getMachineStackPower() {
        return getRegularMachinePower() * (getStackAmount() + 1);
    }

    @Override
    public void tickMachine() {
        if (getMachineTile().getLong("TeslaPower") == 0L || getMachineTile().getBoolean("paused")) return;
        getMachineTile().getCompound("work_energy").setLong("TeslaInput", getMachineStackPower());
    }
}
