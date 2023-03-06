package com.conorsmine.net.industrialstacking.cmd;

import com.conorsmine.net.industrialstacking.IndustrialStacking;
import com.conorsmine.net.industrialstacking.machinestack.MachineStack;
import com.conorsmine.net.industrialstacking.machinestack.StackableMachines;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@SuppressWarnings("ALL")
public class MachineListCmd extends Cmd {

    private static final String PERMISSION = "IndustrialStacking.listMachines";

    public MachineListCmd(IndustrialStacking pl) {
        super(pl);
    }

    @Override
    public String getDescription() {
        return "§7Lists all stacked machines";
    }

    @Override
    public String getUsage() {
        return "§3/is list §7[§b<highest,lowest,exactAbsolute,exact,furthest,closest,type> <amount>§7]\n" +
                pl.getPrefix() + "    §7>> §bhighest§7: Sorts the list by the absolute stack amount.\n" +
                pl.getPrefix() + "    §7>> §blowest§7: Sorts the list by the lowest absolute stack amount.\n" +
                pl.getPrefix() + "    §7>> §bexactAbsolute§7: Filters the list for machine who have §3exactly§7 the stack amount specified by §b<amount>§7.\n" +
                pl.getPrefix() + "    §7>> §bexact§7: Same as §bexact §7but, now only compares the actual, allowed, stack size.\n" +
                pl.getPrefix() + "    §7>> §bfurthest§7: Sorts the list by the §3greatest§7 proximity to the player.\n" +
                pl.getPrefix() + "    §7>> §bclosest§7: Sorts the list by the §3closest§7 proximity to the player.\n" +
                pl.getPrefix() + "    §7>> §btype§7: Filters for machines of a specified type.";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(PERMISSION)) { nonPermissionMsg(sender, PERMISSION); return false; }

        Set<Map.Entry<Location, MachineStack>> entries = pl.getStackManager().entrySet();
        final List<Map.Entry<Location, MachineStack>> sortedMachines = entries.stream()
                .filter(entry -> getFilter(args).test(entry.getValue()))
                .sorted((o1, o2) -> getComparator(args, sender).compare(o1.getValue(), o2.getValue()))
                .collect(Collectors.toList());

        sender.sendMessage(String.format("%s§r§7§m-----§r §eList §7§m-----§r", pl.getPrefix()));
        sender.sendMessage(String.format("%s§7Note: Click on \"§3Here§7\" to teleport to the machine.", pl.getPrefix()));
        sender.sendMessage(String.format("%s§7Note: Depending on your scale, it might be that the click option was moved to the left or right.", pl.getPrefix()));
        for (int i = 0; i < sortedMachines.size(); i++) {
            Map.Entry<Location, MachineStack> entry = sortedMachines.get(i);

            Location location = entry.getKey();
            TextComponent locationData = new TextComponent("§3Here");
            locationData.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(location.toString()).create()));
            locationData.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                    String.format("/tp @s %s %s %s", location.getBlockX(), location.getBlockY() + 1, location.getBlockZ())));

            ComponentBuilder componentBuilder = new ComponentBuilder(String.format("%s§7Type: §3%s §7Amount: §3%d ",
                    pl.getPrefix(), entry.getValue().getMachineEnum(), entry.getValue().getAbsoluteStackAmount()));
            componentBuilder.append(String.format("§7Location: %s", ((i % 2) == 0) ? "" : "        "));
            componentBuilder.append(locationData);

            sender.spigot().sendMessage(componentBuilder.create());
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 3 && args[1].toLowerCase(Locale.ROOT).equals("type")) return Arrays.stream(StackableMachines.values()).map(StackableMachines::name).collect(Collectors.toList());
        if (args.length != 2) return null;
        return new ArrayList<>(Arrays.asList("highest", "lowest", "exact", "exactAbsolute", "closest", "furthest", "type"));
    }

    private Predicate<MachineStack> getFilter(String[] args) {
        if (args.length < 3) return (x -> true);

        final Set<String> machineEnumSet = Arrays.stream(StackableMachines.values()).map(StackableMachines::name).collect(Collectors.toSet());
        if (args[1].toLowerCase(Locale.ROOT).equals("type")) {
            final String typeName = args[2].toUpperCase(Locale.ROOT);
            return (x -> {
                if (!machineEnumSet.contains(typeName)) return false;
                final StackableMachines machineEnum = StackableMachines.valueOf(typeName);
                return x.getMachineEnum().equals(machineEnum);
            });
        }

        if (!args[2].matches("\\d+")) return (x -> true);
        int amount = Integer.parseInt(args[2]);

        if (args[1].toLowerCase(Locale.ROOT).equals("exact")) return (x -> x.getStackAmount() == amount);
        if (args[1].toLowerCase(Locale.ROOT).equals("exactabsolute")) return (x -> x.getAbsoluteStackAmount() == amount);

        return (x -> true);
    }

    private Comparator<MachineStack> getComparator(String[] args, CommandSender sender) {
        if (args.length < 2) return new AlphabeticalComparator();
        if (args[1].toLowerCase(Locale.ROOT).equals("highest")) return new HighestComparator();
        if (args[1].toLowerCase(Locale.ROOT).equals("lowest")) return new LowestComparator();
        if (args[1].toLowerCase(Locale.ROOT).equals("closest"))
            if (sender instanceof Player) return new ClosestComparator(((Player) sender).getLocation());
            else sender.sendMessage("%s§cKeyword §7\"§closest§7\" §ccan only be used by online players.");
        if (args[1].toLowerCase(Locale.ROOT).equals("furthest"))
            if (sender instanceof Player) return new FurthestComparator(((Player) sender).getLocation());
            else sender.sendMessage("%s§cKeyword §7\"§3furthest§7\" §ccan only be used by online players.");

        return new AlphabeticalComparator();
    }

    private static final class AlphabeticalComparator implements Comparator<MachineStack> {

        @Override
        public int compare(MachineStack o1, MachineStack o2) {
            return String.CASE_INSENSITIVE_ORDER.compare(o1.getMachineEnum().name(), o2.getMachineEnum().name());
        }
    }

    private static final class HighestComparator implements Comparator<MachineStack> {

        @Override
        public int compare(MachineStack o1, MachineStack o2) {
            if (o1.getAbsoluteStackAmount() < o2.getAbsoluteStackAmount()) return 1;
            if (o1.getAbsoluteStackAmount() > o2.getAbsoluteStackAmount()) return -1;
            return 0;
        }
    }

    private static final class LowestComparator implements Comparator<MachineStack> {

        @Override
        public int compare(MachineStack o1, MachineStack o2) {
            if (o1.getAbsoluteStackAmount() > o2.getAbsoluteStackAmount()) return 1;
            if (o1.getAbsoluteStackAmount() < o2.getAbsoluteStackAmount()) return -1;
            return 0;
        }
    }

    private static final class ClosestComparator implements Comparator<MachineStack> {

        private final Location playerLocation;

        public ClosestComparator(Location playerLocation) {
            this.playerLocation = playerLocation;
        }

        @Override
        public int compare(MachineStack o1, MachineStack o2) {
            double distance1 = o1.getBlock().getLocation().distance(playerLocation);
            double distance2 = o2.getBlock().getLocation().distance(playerLocation);
            if (distance1 > distance2) return 1;
            if (distance1 < distance2) return -1;
            return 0;
        }
    }

    private static final class FurthestComparator implements Comparator<MachineStack> {

        private final Location playerLocation;

        public FurthestComparator(Location playerLocation) {
            this.playerLocation = playerLocation;
        }

        @Override
        public int compare(MachineStack o1, MachineStack o2) {
            double distance1 = o1.getBlock().getLocation().distance(playerLocation);
            double distance2 = o2.getBlock().getLocation().distance(playerLocation);
            if (distance1 < distance2) return 1;
            if (distance1 > distance2) return -1;
            return 0;
        }
    }
}
