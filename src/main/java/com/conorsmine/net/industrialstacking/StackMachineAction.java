package com.conorsmine.net.industrialstacking;

import com.conorsmine.net.industrialstacking.machinestack.MachineStack;
import com.conorsmine.net.industrialstacking.machinestack.StackableMachines;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

/**
 * Class representing trying to stack machines
 */
public class StackMachineAction {

    private final IndustrialStacking pl;
    private final Player p;
    private final Block block;
    private final ItemStack item;
    private final MachineStack machineStack;
    private final StackableMachines machineEnums;
    private final Integer maxStackSize;

    private final PlayerInteractEvent event;

    public StackMachineAction(IndustrialStacking plugin, PlayerInteractEvent event) {
        this.pl = plugin;
        this.p = event.getPlayer();
        this.block = event.getClickedBlock();
        this.item = event.getItem();

        this.event = event;

        this.machineStack = pl.getStackManager().get(block.getLocation());
        this.machineEnums = StackableMachines.machineFromType(block.getType());

        if (machineEnums == null) this.maxStackSize = 0;
        else this.maxStackSize = pl.getMachineConfigFile().getMaxStackSizeMap().get(machineEnums.getConfigName());
    }

    public void configureMachineStack() {
        if (!isAddableItem()) return;

        if (pl.getStackManager().containsKey(block.getLocation())) addToMachineStack();
        else createMachineStack();

        event.setCancelled(true);
    }

    private boolean isAddableItem() {
        final String blockType = block.getType().name().toUpperCase(Locale.ROOT);
        if (!blockType.matches("INDUSTRIALFOREGOING_.+")) return false;
        if (item == null || item.getType() == Material.AIR) return false;
        if (!StackableMachines.getMatNameSet().contains(blockType)) { cantStackMachineMsg(); return false; }
        return (item.getType().name().equals(block.getType().name()));
    }

    private void addToMachineStack() {
        if (machineStack == null || machineEnums == null) return;
        if (maxStackSize == null || maxStackSize == 0) { cantStackMachineMsg(); return; }
        if (maxStackSize != -1 && machineStack.getStackAmount() >= maxStackSize) { stackLimitReachedMsg(); return; }

        machineStack.addMachineToStack();
        saveMachineToFile(machineStack);
        removeItem();
    }

    private void createMachineStack() {
        if (machineEnums == null) return;
        if (maxStackSize == null || maxStackSize == 0) { cantStackMachineMsg(); return; }

        final MachineStack machineStack = machineEnums.createNew(block);
        if (machineStack == null) return;
        pl.getStackManager().put(block.getLocation(), machineStack);
        saveMachineToFile(machineStack);
        removeItem();
    }

    private void removeItem() {
        item.setAmount(item.getAmount() - 1);
    }

    private void cantStackMachineMsg() {
        p.sendMessage(String.format("%s §cCan't stack §b%s §cmachines!§r", pl.getPrefix(), block.getType().name()));
    }

    private void stackLimitReachedMsg() {
        p.sendMessage(String.format("%s §3Stack limit of §l§b%d §r§3reached!§r",
                pl.getPrefix(),
                pl.getMachineConfigFile().getMaxStackSizeMap().get(machineEnums.getConfigName())));
    }

    private void saveMachineToFile(MachineStack machineStack) {
        pl.getMachineSaveFile().saveMachineStack(machineStack);
    }
}
