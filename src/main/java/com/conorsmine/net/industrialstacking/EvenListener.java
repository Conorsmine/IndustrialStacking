package com.conorsmine.net.industrialstacking;

import com.conorsmine.net.industrialstacking.machinestack.MachineStack;
import com.conorsmine.net.industrialstacking.machinestack.StackableMachines;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class EvenListener implements Listener {

    private final IndustrialStacking pl;

    public EvenListener(IndustrialStacking pl) {
        this.pl = pl;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getPlayer() == null) return;  // In case a modded machine tries it
        if (event.getHand() != EquipmentSlot.HAND) return;

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getPlayer().isSneaking() && !event.hasItem()) displayMachineStackSize(event);
            else new StackMachineAction(pl, event).configureMachineStack();
        }
        else if (event.getAction() == Action.LEFT_CLICK_BLOCK &&
                event.getPlayer().isSneaking() && !event.hasItem())
            removeMachineFromStack(event);
    }

    private void displayMachineStackSize(PlayerInteractEvent event) {
        final MachineStack machineStack = pl.getStackManager().get(event.getClickedBlock().getLocation());
        if (machineStack == null) return;

        final Player player = event.getPlayer();
        player.sendMessage(String.format("%s §3This machine contains §b§l%d §r§3more machines.§r",
                pl.getPrefix(), machineStack.getStackAmount()));


        if (player.isOp() && (machineStack.getAbsoluteStackAmount() > machineStack.getStackAmount()))
            player.sendMessage(String.format("%s §7>> Absolute amount of stacked machines: §3%s§r",
                    pl.getPrefix(), machineStack.getAbsoluteStackAmount()));

        event.setCancelled(true);
    }

    /**
     * Removes one machine from the stack
     */
    private void removeMachineFromStack(PlayerInteractEvent event) {
        final MachineStack machineStack = pl.getStackManager().get(event.getClickedBlock().getLocation());
        if (machineStack == null) return;

        final ItemStack machineItem = machineStack.getMachineItemStack();
        machineItem.setAmount(1);
        if (machineStack.removeMachineFromStack()) machineStack.removeMachineStack();   // Machine doesn't exist anymore

        final Player player = event.getPlayer();
        final HashMap<Integer, ItemStack> overflowItems = player.getInventory().addItem(machineItem);
        if (!overflowItems.isEmpty()) player.getLocation().getWorld().dropItem(player.getLocation(), machineItem);

        player.sendMessage(String.format("%s §3Removed item from stacked machine.§r", pl.getPrefix()));
        event.setCancelled(true);
    }




    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onBlockBreak(BlockBreakEvent event) {
        dropStackMachineItems(event.getBlock().getLocation());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onBlockExplode(BlockExplodeEvent event) {
        dropStackMachineItems(event.getBlock().getLocation());
    }

    private void dropStackMachineItems(Location location) {
        if (!pl.getStackManager().containsKey(location)) return;
        final MachineStack machineStack = pl.getStackManager().get(location);
        machineStack.removeMachineStack();
        location.getWorld().dropItem(location, machineStack.getMachineItemStack());
    }
}
