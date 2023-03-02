package com.conorsmine.net.industrialstacking.machinestack.industrialforegoing.machines;

import com.conorsmine.net.industrialstacking.IndustrialStacking;
import com.conorsmine.net.industrialstacking.machinestack.MachineStack;
import com.conorsmine.net.industrialstacking.machinestack.Powerable;
import com.conorsmine.net.industrialstacking.machinestack.StackableMachines;
import com.conorsmine.net.industrialstacking.machinestack.industrialforegoing.ForegoingUtils;
import de.tr7zw.nbtapi.NBTCompoundList;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class LaserDrill extends MachineStack implements Powerable {

    public LaserDrill(@NotNull IndustrialStacking plugin, @NotNull Block tileEntity) {
        super(plugin, tileEntity, StackableMachines.LASER_DRILL);
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
        getMachineTile().getCompound("work_energy").setLong("TeslaInput", getMachineStackPower());
    }
}
