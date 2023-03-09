package com.conorsmine.net.industrialstacking.cmd;

import com.conorsmine.net.industrialstacking.IndustrialStacking;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.*;

public class IndustrialStackingCmd implements TabExecutor {

    private final IndustrialStacking pl;
    private final Map<String, Cmd> subCommandMap = new HashMap<>();

    public IndustrialStackingCmd(IndustrialStacking pl) {
        this.pl = pl;

        subCommandMap.put("hidtp", new HiddenTeleportCmd(pl));
        subCommandMap.put("profiler", new ProfilerCmd(pl));
        subCommandMap.put("profilerinfo", new ProfilerInfoCmd(pl));
        subCommandMap.put("info", new InfoCmd(pl));
        subCommandMap.put("view", new MachineViewCmd(pl));
        subCommandMap.put("list", new MachineListCmd(pl));
        subCommandMap.put("reload", new ReloadCmd(pl));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) { sendUsageMsg(sender); return false; }

        Cmd cmd = subCommandMap.get(args[0].toLowerCase(Locale.ROOT));
        if (cmd == null) return false;
        return cmd.onCommand(sender, command, label, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length <= 1) return new LinkedList<>(subCommandMap.keySet());

        Cmd cmd = subCommandMap.get(args[0].toLowerCase(Locale.ROOT));
        if (cmd == null) return null;
        return cmd.onTabComplete(sender, command, alias, args);
    }

    private void sendUsageMsg(CommandSender sender) {
        sender.sendMessage(String.format("%s§r§7§m-----§r §6§lUsage§r §7§m-----§r", pl.getPrefix()));
        sender.sendMessage(String.format("%s§7Aliases: \"§3is§7\" or \"§3istk§7\".", pl.getPrefix()));
        sender.sendMessage(String.format("%s§7Sub-commands:§r", pl.getPrefix()));

        for (Map.Entry<String, Cmd> cmdEntry : subCommandMap.entrySet()) {
            sender.sendMessage(String.format("%s  §7Usage: §r%s§r", pl.getPrefix(), cmdEntry.getValue().getUsage()));
            sender.sendMessage(String.format("%s  §7>>§r %s§r", pl.getPrefix(), cmdEntry.getValue().getDescription()));
        }
    }
}
