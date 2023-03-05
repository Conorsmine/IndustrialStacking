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
    private final StackableMachines machineEnum;
    private final Material machineType;
    private final NBTTileEntity tileEntity;
    private int stackAmount = 1;

    public MachineStack(@NotNull IndustrialStacking plugin, @NotNull Block tileEntity, @NotNull StackableMachines machineEnum) {
        this.pl = plugin;
        this.block = tileEntity;
        this.tileEntity = new NBTTileEntity(tileEntity.getState());
        this.machineEnum = machineEnum;
        this.machineType = machineEnum.getMaterial();
    }

    /**
     *  This function can be used to add some logic before ticking the machine
     */
    public void tick() {
        tickMachine();
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
     * @return The enum of the machine, as represented via the {@link StackableMachines} enum.
     */
    public StackableMachines getMachineEnum() {
        return machineEnum;
    }

    /**
     * @return The material of the machine.
     */
    public Material getMachineType() {
        return machineType;
    }

    /**
     * @return The amount of machines in the stack, clamped by the config
     */
    public int getStackAmount() {
        return Math.min(this.stackAmount, pl.getMachineConfigFile().getMaxStackSizeMap().get(machineEnum.getConfigName()));
    }

    /**
     * @return Absolute amount of stacked machines
     */
    public int getAbsoluteStackAmount() {
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
        final int idOffset = pl.getMachineConfigFile().getIdOffsetMap().getOrDefault(machineEnum.getModFromMachine(), 0);
        final int machineId = machineType.getId() + idOffset;
        return new ItemStack(machineId, getStackAmount());
    }

    /**
     * Removes this machine stack.
     */
    public void removeMachineStack() {
        pl.getStackManager().remove(getBlock().getLocation());
        pl.getMachineSaveFile().removeMachineStack(this);
    }

    public IndustrialStacking getPl() {
        return pl;
    }

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
