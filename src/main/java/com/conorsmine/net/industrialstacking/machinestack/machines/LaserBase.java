package com.conorsmine.net.industrialstacking.machinestack.machines;

import com.conorsmine.net.industrialstacking.machinestack.MachineStack;
import com.conorsmine.net.industrialstacking.machinestack.StackableMachines;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class LaserBase extends MachineStack {

    private ItemStack[] baseInvPrev;

    public LaserBase(@NotNull Block tileEntity) {
        super(tileEntity, StackableMachines.LASER_BASE.getMaterial());
        this.baseInvPrev = getBaseItems();
    }

    @Override
    public long getRegularMachinePower() {
        // Doesn't use energy, so ignore it
        return 0;
    }

    @Override
    public void tickMachine() {
        final Long currentWork = this.getMachineTile().getLong("currentWork");
        if (currentWork >= 2) return;
        System.out.println(Arrays.toString(baseInvPrev));
        final ItemStack[] baseInvCurrent = getBaseItems();
        System.out.println(Arrays.toString(baseInvCurrent));
        System.out.println("Mined: " + getMinedItem(baseInvCurrent));
        baseInvPrev = baseInvCurrent.clone();
    }

    private ItemStack[] getBaseItems() {
        final ItemStack[] outItems = getMachineTile().getCompound("outItems").getItemStackArray("Items");
        if (outItems == null) return new ItemStack[0];
        return outItems;
    }

    private Material getMinedItem(ItemStack[] baseInv) {
        for (ItemStack prevItem : baseInvPrev) {
            for (ItemStack item : baseInv) {
                if (prevItem.getType() != item.getType()) continue;
                if (item.getAmount() > prevItem.getAmount())
                    return item.getType();
            }
        }

        // Ig no item was added?
        return null;
    }
}
