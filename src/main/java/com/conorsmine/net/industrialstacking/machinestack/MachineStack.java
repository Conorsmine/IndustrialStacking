package com.conorsmine.net.industrialstacking.machinestack;

import com.conorsmine.net.industrialstacking.IndustrialStacking;
import de.tr7zw.nbtapi.NBTTileEntity;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract class representing the stack of a machine
 */
public abstract class MachineStack {

    private final IndustrialStacking pl;
    private final Block block;
    private final Material machineType;
    private final NBTTileEntity tileEntity;
    private int stackAmount = 1;

    public MachineStack(@NotNull IndustrialStacking plugin, @NotNull Block tileEntity, @NotNull Material machineType) {
        this.pl = plugin;
        this.block = tileEntity;
        this.tileEntity = new NBTTileEntity(tileEntity.getState());
        this.machineType = machineType;
    }

    /**
     *  This function can be used to add some logic before ticking the machine
     */
    public void tick() {
        tickMachine();
    }

    public IndustrialStacking getPlugin() {
        return pl;
    }

    /**
     * @return The block of the machine
     */
    public Block getBlock() {
        return block;
    }

    /**
     * The NBT of the TileEntity can be manipulated directly and is updated.
     * @return The NBT of the machine
     */
    public NBTTileEntity getMachineTile() {
        return this.tileEntity;
    }

    /**
     * @return The material of the machine.
     */
    public Material getMachineType() {
        return machineType;
    }

    /**
     * @return The amount of machines in the stack
     */
    public int getStackAmount() {
        return this.stackAmount;
    }

    /**
     * Increases the stack by 1
     */
    public void addMachineToStack() {
        this.stackAmount++;
    }

    /**
     * @param amount The amount of stacked machines
     */
    public void setMachineStackAmount(int amount) {
        this.stackAmount = amount;
    }

    /**
     * @return The amount of items that should be returned when the block breaks or explodes.
     */
    public ItemStack getMachineItemStack() {
        // Todo:
        //  This may cause issues, idk yet, needs further testing!
        final int machineId = machineType.getId() + 3907;
        return new ItemStack(machineId, getStackAmount());
    }

    /**
     * @return The amount of power required by the machine stack.
     */
    public long getMachineStackPower() {
        // Add 1, as the machine itself counts too
        return getRegularMachinePower() * (getStackAmount() + 1);
    }

    /**
     * Removes this machine stack.
     */
    public void removeMachineStack() {
        pl.getStackManager().remove(getBlock().getLocation());
        pl.getMachineSaveFile().removeMachineStack(this);
    }

    /**
     * To increase the machines speed, change the amount of energy the machine
     * can input. To calculate it correctly though, a value is needed, before
     * all other machines increase the power input.
     * @return Power input, before recalculation
     */
    public abstract long getRegularMachinePower();

    /**
     * Function which will run every tick, normally it will be to increase the power input of the machine.
     */
    public abstract void tickMachine();

    @Override
    public String toString() {
        return "MachineStack{" +
                "machineType=" + machineType +
                ", stackAmount=" + stackAmount +
                '}';
    }
}
