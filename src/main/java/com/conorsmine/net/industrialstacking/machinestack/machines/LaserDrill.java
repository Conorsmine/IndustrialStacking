package com.conorsmine.net.industrialstacking.machinestack.machines;

import com.conorsmine.net.industrialstacking.machinestack.MachineStack;
import com.conorsmine.net.industrialstacking.machinestack.StackableMachines;
import de.tr7zw.nbtapi.NBTCompoundList;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class LaserDrill extends MachineStack {

    public LaserDrill(Block tileEntity) {
        super(tileEntity, StackableMachines.LASER_DRILL.getMaterial());
    }

    @Override
    public long getRegularMachinePower() {
        final NBTCompoundList compoundList = getMachineTile().getCompound("addonItems").getCompoundList("Items");
        final boolean speedOne = contains("teslacorelib:speed_tier1", compoundList);
        final boolean speedTwo = contains("teslacorelib:speed_tier2", compoundList);

        if (speedTwo && speedOne) return 225;
        if (speedOne) return 150;
        else return  100;
    }

    @Override
    public void tickMachine() {
        getMachineTile().getCompound("work_energy").setLong("TeslaInput", getMachineStackPower());
    }

    private boolean contains(String itemId, NBTCompoundList compoundList) {
        for (ReadWriteNBT nbt : compoundList) {
            if (nbt.getString("id").equals(itemId))
                return true;
        }

        return false;
    }
}
