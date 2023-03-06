package com.conorsmine.net.industrialstacking.cmd;

import com.conorsmine.net.industrialstacking.IndustrialStacking;
import com.conorsmine.net.industrialstacking.machinestack.MachineStack;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MachineViewCmd extends Cmd {

    private static final String PERMISSION = "IndustrialStacking.viewMachines";
    private static final double MAX_VIEW_DISTANCE = 20;
    private static final double STEP_SIZE = 0.3;
    private static final Vector[] DIRECTIONS = new Vector[3];
    static {
        DIRECTIONS[0] = new Vector(0, STEP_SIZE, 0);
        DIRECTIONS[1] = new Vector(0, 0, STEP_SIZE);
        DIRECTIONS[2] = new Vector(STEP_SIZE, 0, 0);
    }

    private final Set<Integer> viewRunnables = new HashSet<>();

    public MachineViewCmd(IndustrialStacking pl) {
        super(pl);
    }

    @Override
    public String getDescription() {
        return "§7Adds a border to close by machines";
    }

    @Override
    public String getUsage() {
        return "§3/is view §7[§b<duration is seconds, clear>§7]\n" +
                pl.getPrefix() + "    §7>> §bclear§7: Removes all §chighlighting §7from stacked machines.";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(PERMISSION)) { nonPermissionMsg(sender, PERMISSION); return false; }

        sender.sendMessage(String.format("%s§r§7§m-----§r §eView §7§m-----§r", pl.getPrefix()));
        if (args.length >= 2 && args[1].toLowerCase(Locale.ROOT).equals("clear"))
            runClearCommand(sender);
        else {
            if (!(sender instanceof Player)) { sender.sendMessage("%s§cCan only be used by online players."); return false; }
            runViewCommand((Player) sender, args);
        }

        return true;
    }

    private void runClearCommand(CommandSender sender) {
        for (Integer viewRunnable : viewRunnables)
            Bukkit.getScheduler().cancelTask(viewRunnable);

        sender.sendMessage(String.format("%s§7Cancelled all machine §chighlighting§7.§r", pl.getPrefix()));
    }

    private void runViewCommand(Player sender, String[] args) {
        Location pLoc = sender.getLocation();
        for (Map.Entry<Location, MachineStack> entry : pl.getStackManager().entrySet()) {
            if (!entry.getKey().getWorld().equals(pLoc.getWorld())) continue;
            if (pLoc.distance(entry.getKey()) > MAX_VIEW_DISTANCE) continue;

            int duration = 10;
            if (args.length >= 2 && args[1].length() <= 9 && args[1].matches("\\d+"))
                duration = Integer.parseInt(args[1]);

            spawnParticles(pLoc, entry.getKey(), duration);
        }

        sender.sendMessage(String.format("%s§cHighlighting §7stacked machines.§r", pl.getPrefix()));
    }

    private void spawnParticles(Location playerLocation, Location mLoc, int durationSeconds) {
        final Location cornerA = new Location(mLoc.getWorld(), mLoc.getBlockX(), mLoc.getY(), mLoc.getBlockZ());
        final Location cornerB = new Location(mLoc.getWorld(), mLoc.getBlockX() + 1, mLoc.getY() + 1, mLoc.getBlockZ() + 1);
        final Location pLoc = playerLocation.clone();

        AtomicInteger count = new AtomicInteger(0);
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(pl, () -> {
            if (count.getAndIncrement() >= durationSeconds * 10) return;

            for (int i = 0; i < (1 / STEP_SIZE); i++) {
                for (Vector direction : DIRECTIONS) {
                    pLoc.getWorld().spawnParticle(Particle.REDSTONE, cornerA.clone().add(direction.clone().multiply(i)), 0);
                    pLoc.getWorld().spawnParticle(Particle.REDSTONE, cornerB.clone().add(direction.clone().multiply(-i)), 0);
                }
            }
        }, 0L, 2L);

        viewRunnables.add(bukkitTask.getTaskId());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
