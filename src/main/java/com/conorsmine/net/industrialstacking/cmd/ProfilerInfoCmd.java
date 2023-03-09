package com.conorsmine.net.industrialstacking.cmd;

import com.conorsmine.net.industrialstacking.IndustrialStacking;
import com.conorsmine.net.industrialstacking.StackProfiler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ProfilerInfoCmd extends Cmd {

    private static final String PERMISSION = "IndIndustrialStacking.profiler";

    public ProfilerInfoCmd(IndustrialStacking pl) {
        super(pl);
    }

    @Override
    public String getDescription() {
        return "§7Provides information about the usage of §3/is profiler§r";
    }

    @Override
    public String getUsage() {
        return "§3/is profilerInfo";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(PERMISSION)) { nonPermissionMsg(sender, PERMISSION); return false; }

        sender.sendMessage(String.format(getCmdHeader(), "Profiler-Info"));
        sender.sendMessage(String.format("%s§7The main command: §3/is profiler§r", pl.getPrefix()));
        sender.sendMessage(String.format("%s§7This command can be extended by the following tags...", pl.getPrefix()));

        // tags...
        sender.sendMessage(String.format("%s  §3%s §b<tickDuration>§r", pl.getPrefix(), ProfilerCmd.CommandTags.TIME_TICK.commandTag));
        sender.sendMessage(String.format("%s  §7>> Runs the profiler for the specified §bduration in ticks§7", pl.getPrefix()));
        sender.sendMessage(String.format("%s  §7>> Example: §3/is profiler §b%s 200", pl.getPrefix(), ProfilerCmd.CommandTags.TIME_TICK.commandTag));

        sender.sendMessage(String.format("%s  §3%s §b<secondsDuration>§r", pl.getPrefix(), ProfilerCmd.CommandTags.TIME_SECOND.commandTag));
        sender.sendMessage(String.format("%s  §7>> Runs the profiler for the specified §bduration in seconds§7", pl.getPrefix()));
        sender.sendMessage(String.format("%s  §7>> Example: §3/is profiler §b%s 10", pl.getPrefix(), ProfilerCmd.CommandTags.TIME_SECOND.commandTag));

        sender.sendMessage(String.format("%s  §3%s §b<fileName>§r", pl.getPrefix(), ProfilerCmd.CommandTags.LOG.commandTag));
        sender.sendMessage(String.format("%s  §7>> Saves the profiler data to the specified §bfile§7", pl.getPrefix()));
        sender.sendMessage(String.format("%s  §7>> Example: §3/is profiler §b%s performance", pl.getPrefix(), ProfilerCmd.CommandTags.LOG.commandTag));

        sender.sendMessage(String.format("%s  §3%s §b<range>§r", pl.getPrefix(), ProfilerCmd.CommandTags.NEARBY.commandTag));
        sender.sendMessage(String.format("%s  §7>> Returns the profiler data for machines within §brange§7", pl.getPrefix()));
        sender.sendMessage(String.format("%s  §7>> Example: §3/is profiler §b%s 5", pl.getPrefix(), ProfilerCmd.CommandTags.NEARBY.commandTag));

        sender.sendMessage(String.format("%s  §3%s §r", pl.getPrefix(), ProfilerCmd.CommandTags.SORT.commandTag));
        sender.sendMessage(String.format("%s  §7>> Sorts the return data in alphabetical order of machine types", pl.getPrefix()));
        sender.sendMessage(String.format("%s  §7>> Example: §3/is profiler §b%s", pl.getPrefix(), ProfilerCmd.CommandTags.SORT.commandTag));

        sender.sendMessage(String.format("%s  §3%s §r", pl.getPrefix(), ProfilerCmd.CommandTags.HIGH.commandTag));
        sender.sendMessage(String.format("%s  §7>> Sorts the return data for laggiest machines", pl.getPrefix()));
        sender.sendMessage(String.format("%s  §7>> Example: §3/is profiler §b%s", pl.getPrefix(), ProfilerCmd.CommandTags.HIGH.commandTag));

        sender.sendMessage(String.format("%s  §3%s §r", pl.getPrefix(), ProfilerCmd.CommandTags.LOW.commandTag));
        sender.sendMessage(String.format("%s  §7>> Sorts the return data for friendliest machines", pl.getPrefix()));
        sender.sendMessage(String.format("%s  §7>> Example: §3/is profiler §b%s", pl.getPrefix(), ProfilerCmd.CommandTags.LOW.commandTag));

        sender.sendMessage(String.format("%s  §3%s §r", pl.getPrefix(), ProfilerCmd.CommandTags.COMBINE.commandTag));
        sender.sendMessage(String.format("%s  §7>> Combines machines of the same type", pl.getPrefix()));
        sender.sendMessage(String.format("%s  §7>> Example: §3/is profiler §b%s", pl.getPrefix(), ProfilerCmd.CommandTags.COMBINE));

        sender.sendMessage(String.format("%s  §3%s §r", pl.getPrefix(), ProfilerCmd.CommandTags.STOP.commandTag));
        sender.sendMessage(String.format("%s  §7>> Stops the previous profiler and runs the current one", pl.getPrefix()));
        sender.sendMessage(String.format("%s  §7>> Example: §3/is profiler §b%s", pl.getPrefix(), ProfilerCmd.CommandTags.STOP.commandTag));
        sender.sendMessage(String.format("%s  §7>> Using \"§3/is profiler §cstop§7\" will NOT run another profiler", pl.getPrefix()));

        sender.sendMessage(pl.getPrefix());
        sender.sendMessage(String.format("%s§7Note: Should multiple tags collide, e.g.:", pl.getPrefix()));
        sender.sendMessage(String.format("%s  §7>> §3/is profiler -low -high", pl.getPrefix()));
        sender.sendMessage(String.format("%s§7then the §3last tag §7will be considered.", pl.getPrefix()));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
