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

import java.util.Locale;
import java.util.Objects;

public class EvenListener implements Listener {

    private final IndustrialStacking pl;

    public EvenListener(IndustrialStacking pl) {
        this.pl = pl;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getPlayer() == null) return;  // In case a modded machine tries it
        if (event.getHand() != EquipmentSlot.HAND) return;

        if (event.getPlayer().isSneaking() && !event.hasItem()) displayMachineStackSize(event);
        else new StackMachineAction(pl, event).configureMachineStack();
    }

    private void displayMachineStackSize(PlayerInteractEvent event) {
        final MachineStack machineStack = pl.getStackManager().get(event.getClickedBlock().getLocation());
        if (machineStack == null) return;

        event.getPlayer().sendMessage(String.format("%s §3This machine contains §b§l%d §r§3more machines.§r",
                pl.getPrefix(), machineStack.getStackAmount()));

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
