package com.conorsmine.net.industrialstacking.cmd;

import com.conorsmine.net.industrialstacking.IndustrialStacking;
import com.conorsmine.net.industrialstacking.StackProfiler;
import com.conorsmine.net.industrialstacking.Tuple;
import com.conorsmine.net.industrialstacking.machinestack.MachineStack;
import com.conorsmine.net.industrialstacking.machinestack.StackableMachines;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProfilerCmd extends Cmd {

    protected static final String PERMISSION = "IndIndustrialStacking.profiler";

    public ProfilerCmd(IndustrialStacking pl) {
        super(pl);
    }

    @Override
    public String getDescription() {
        return "§7Provides information about the plugins performance";
    }

    @Override
    public String getUsage() {
        return "§3/is profiler\n" +
                pl.getPrefix() + "    §7>> Use §3/is profilerInfo §7for more info";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(PERMISSION)) { nonPermissionMsg(sender, PERMISSION); return false; }

        final List<Tuple<CommandTags, List<String>>> commandTagArgs = getCommandTagArgs(args);
        final StackProfiler stackProfiler = profilerBuilder(commandTagArgs);
        sender.sendMessage(String.format(getCmdHeader(), "Profiler"));
        sender.sendMessage(String.format("%s§7Running profiler for: §3%d ticks", pl.getPrefix(), stackProfiler.getTickDuration()));

        final CompletableFuture<StackProfiler.ProfilerResult> completableProfiler = stackProfiler.runProfiler();
        completableProfiler.whenComplete((p, exc) -> {
            final List<Map.Entry<MachineStack, List<Long>>> list = processCommandTags(commandTagArgs, p, sender).collect(Collectors.toList());
            final double totalMillis = nanoTimeToMillis(getAbsoluteTimeNano(list));
            final double totalMillisPerTick = totalMillis / p.getTickDuration();
            final BaseComponent[] v = formatData(list, p.getTickDuration());

            sender.sendMessage(String.format("%s§eResults:", pl.getPrefix()));
            sender.sendMessage(String.format("%s§7Processing time: §3%,.3fms§7; §3%,.3fms§7 per tick; §3%,.3f%% §7of this tick.",
                    pl.getPrefix(), totalMillis, totalMillisPerTick, ((totalMillisPerTick / 50) * 100)));

            for (int i = 0; i < Math.floor(((double) v.length) / 6); i++) {
                int j = i * 6;
                final BaseComponent[] msg = new BaseComponent[] { v[j], v[++j], v[++j], v[++j], v[++j], v[++j] };
                sender.spigot().sendMessage(msg);
            }
        });
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }

    private List<Tuple<CommandTags, List<String>>> getCommandTagArgs(String[] args) {
        final List<Tuple<CommandTags, List<String>>> cmdArgsList = new LinkedList<>();
        for (int i = 1; i < args.length; i++) {
            final String cmdArg = args[i];

            if (!CommandTags.tagSet.contains(cmdArg.toLowerCase(Locale.ROOT))) continue;

            final List<String> tagArgsList = new LinkedList<>();
            cmdArgsList.add(new Tuple<>(CommandTags.getTagFromArg(cmdArg), tagArgsList));
            for (int j = i + 1; j < args.length; j++) {
                final String tagArg = args[j];

                if (CommandTags.tagSet.contains(tagArg.toLowerCase(Locale.ROOT))) {
                    i = --j;
                    break;
                }

                tagArgsList.add(tagArg);
                if (j >= (args.length) - 1) break;
            }
        }

        return cmdArgsList;
    }

    private StackProfiler profilerBuilder(List<Tuple<CommandTags, List<String>>> commandTagArgsList) {
        int tickDuration = 100;
        for (int i = commandTagArgsList.size() - 1; i >= 0; i--) {
            final Tuple<CommandTags, List<String>> tuple = commandTagArgsList.get(i);

            if (tuple.getT() == CommandTags.TIME_TICK) {
                if (!isValidNumber(tuple.getE())) continue;
                tickDuration = Integer.parseInt(tuple.getE().get(0));
                break;
            }
            if (tuple.getT() == CommandTags.TIME_SECOND) {
                if (!isValidNumber(tuple.getE())) continue;
                tickDuration = (Integer.parseInt(tuple.getE().get(0)) * 20);
                break;
            }
        }

        return new StackProfiler(pl, tickDuration);
    }

    private boolean isValidNumber(List<String> tagArgsList) {
        if (tagArgsList.size() == 0) return false;
        if (!tagArgsList.get(0).matches("\\d+")) return false;
        if (tagArgsList.get(0).length() > 9) return false;
        return true;
    }

    private Stream<Map.Entry<MachineStack, List<Long>>> processCommandTags(List<Tuple<CommandTags, List<String>>> commandTagArgsList,
                                                                           StackProfiler.ProfilerResult result, CommandSender sender) {
        Stream<Map.Entry<MachineStack, List<Long>>> stream = result.getNanoTimePerMachine().entrySet().stream();

        for (Tuple<CommandTags, List<String>> tuple : commandTagArgsList) {
            final CommandTags tag = tuple.getT();
            final List<String> tagArgs = tuple.getE();

            if (tag == CommandTags.TIME_TICK || tag == CommandTags.TIME_SECOND) continue;

            if (tag == CommandTags.LOG && tagArgs.size() > 0) stream = createLogFile(result, stream, tagArgs);
            else if (tag == CommandTags.NEARBY && tagArgs.size() > 0) stream = filterNearby(sender, stream, tagArgs);
            else if (tag == CommandTags.SORT) stream = sortAlphabetically(stream);
            else if (tag == CommandTags.HIGH) stream = sortHighest(result, stream);
            else if (tag == CommandTags.LOW) stream = sortLowest(result, stream);
            else if (tag == CommandTags.COMBINE) stream = combineMachineTypes(stream);
        }

        return stream;
    }

    @NotNull
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private Stream<Map.Entry<MachineStack, List<Long>>> createLogFile(StackProfiler.ProfilerResult result,
                                                                      Stream<Map.Entry<MachineStack, List<Long>>> stream, List<String> tagArgs) {
        final List<Map.Entry<MachineStack, List<Long>>> collect = stream.collect(Collectors.toList());

        try {
            final File logDir = new File((pl.getDataFolder().getPath() + File.separatorChar + "logs"));
            logDir.mkdir();

            final File logFile = new File((pl.getDataFolder().getPath() + File.separatorChar + "logs" + File.separatorChar + tagArgs.get(0) + ".txt"));
            logFile.createNewFile();

            FileOutputStream fos = new FileOutputStream(logFile, true);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();

            fos.write(String.format("---------------[ %s ]---------------%n", dtf.format(now)).getBytes());
            final BaseComponent[] v = formatData(collect, result.getTickDuration());
            for (int i = 0; i < Math.floor(((double) v.length) / 6); i++) {
                int j = i * 6;
                final StringBuilder b = new StringBuilder();
                b.append(v[j].toPlainText()).append(v[++j].toPlainText()).append(v[++j].toPlainText())
                        .append(v[++j].toPlainText()).append(v[++j].toPlainText()).append(v[++j].toPlainText()).append("\n");


                fos.write(b.toString().replaceAll("§.", "").getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return collect.stream();
    }

    @NotNull
    private Stream<Map.Entry<MachineStack, List<Long>>> filterNearby(CommandSender sender, Stream<Map.Entry<MachineStack, List<Long>>> stream, List<String> tagArgs) {
        if (!(sender instanceof Player)) return stream;
        if (!isValidNumber(tagArgs)) return stream;
        int range = Integer.parseInt(tagArgs.get(0));
        Location pLoc = ((Player) sender).getLocation().clone();
        stream = stream.filter((e) -> (e.getKey().getBlock().getLocation().distance(pLoc) <= range));
        return stream;
    }

    @NotNull
    private Stream<Map.Entry<MachineStack, List<Long>>> sortAlphabetically(Stream<Map.Entry<MachineStack, List<Long>>> stream) {
        return stream.sorted(((o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(o1.getKey().getMachineEnum().name(), o2.getKey().getMachineEnum().name())));
    }

    @NotNull
    private Stream<Map.Entry<MachineStack, List<Long>>> sortHighest(StackProfiler.ProfilerResult result, Stream<Map.Entry<MachineStack, List<Long>>> stream) {
        stream = stream.sorted((o1, o2) -> {
            final double averageMillis1 = getAverageMillis(o1.getValue(), result.getTickDuration());
            final double averageMillis2 = getAverageMillis(o2.getValue(), result.getTickDuration());

            if (averageMillis1 < averageMillis2) return 1;
            if (averageMillis1 > averageMillis2) return -1;
            return 0;
        });
        return stream;
    }

    @NotNull
    private Stream<Map.Entry<MachineStack, List<Long>>> sortLowest(StackProfiler.ProfilerResult result, Stream<Map.Entry<MachineStack, List<Long>>> stream) {
        stream = stream.sorted((o1, o2) -> {
            final double averageMillis1 = getAverageMillis(o1.getValue(), result.getTickDuration());
            final double averageMillis2 = getAverageMillis(o2.getValue(), result.getTickDuration());

            if (averageMillis1 > averageMillis2) return 1;
            if (averageMillis1 < averageMillis2) return -1;
            return 0;
        });
        return stream;
    }

    @NotNull
    private Stream<Map.Entry<MachineStack, List<Long>>> combineMachineTypes(Stream<Map.Entry<MachineStack, List<Long>>> stream) {
        final Map<StackableMachines, Tuple<MachineStack, List<Long>>> mapperMap = new HashMap<>();

        // Create mapping of enum -> longs
        final List<Map.Entry<MachineStack, List<Long>>> collect = stream.collect(Collectors.toList());
        if (collect.size() == 0) return stream;
        for (Map.Entry<MachineStack, List<Long>> entry : collect) {
            final StackableMachines machineEnum = entry.getKey().getMachineEnum();
            final Tuple<MachineStack, List<Long>> listTuple = mapperMap.getOrDefault(machineEnum, new Tuple<>(entry.getKey(), new LinkedList<>()));

            List<Long> longs = listTuple.getE();
            longs.addAll(entry.getValue());
            mapperMap.put(machineEnum, listTuple);
        }


        // Create new return stream
        return mapperMap.values().stream().map(machineStackListTuple ->
            new HashMap<MachineStack, List<Long>>() {{ put(machineStackListTuple.getT(), machineStackListTuple.getE()); }}
                    .entrySet().iterator().next()
        );
    }

    @NotNull
    @SuppressWarnings("ComparatorMethodParameterNotUsed")
    private BaseComponent[] formatData(List<Map.Entry<MachineStack, List<Long>>> list, long duration) {
        final ComponentBuilder builder = new ComponentBuilder("");
        final TextComponent clearEvents = new TextComponent();
        clearEvents.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, null));
        clearEvents.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, null));

        for (Map.Entry<MachineStack, List<Long>> entry : list) {
            builder.append(String.format("%s§3%s ", pl.getPrefix(), entry.getKey().getMachineEnum()));

            final Location location = entry.getKey().getBlock().getLocation();
            final TextComponent locationText = new TextComponent(String.format("§7at §3%d %d %d §7in §3%s ",
                    location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getWorld().getName()));
            locationText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§eteleport").create()));
            locationText.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/is hidtp %s %s %s %s",
                    location.getWorld().getUID(), location.getBlockX(), location.getBlockY(), location.getBlockZ())));
            builder.append(locationText);

            builder.append(clearEvents);

            builder.append("§7took an average of ");
            final long highest = entry.getValue().stream().max((o1, o2) -> (o1 >= o2) ? 1 : -1).orElse(0L);
            final long lowest =  entry.getValue().stream().min((o1, o2) -> (o1 >= o2) ? 1 : -1).orElse(0L);

            final TextComponent averageText = new TextComponent(String.format("§3%,.3fms§7 per tick§r", getAverageMillis(entry.getValue(), duration)));
            averageText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder(String.format("§e%,.3fms §7- §e%,.3fms§7 per tick§r", nanoTimeToMillis(highest), nanoTimeToMillis(lowest))).create()));
            builder.append(averageText);

            builder.append(clearEvents);
        }

        return builder.create();
    }

    private double nanoTimeToMillis(long nanoTime) {
        // s -> ms    -> µs        -> ns
        // 1 -> 1_000 -> 1_000_000 -> 1_000_000_000
        return ((double) nanoTime) / 1_000_000;
    }

    private double getAverageMillis(List<Long> allTimes, long duration) {
        long totalNano = 0;
        for (Long time : allTimes)
            totalNano += time;

        return nanoTimeToMillis(totalNano) / duration;
    }

    private long getAbsoluteTimeNano(List<Map.Entry<MachineStack, List<Long>>> list) {
        long totalNano = 0;
        for (Map.Entry<MachineStack, List<Long>> entry : list) {
            for (Long l : entry.getValue()) {
                totalNano += l;
            }
        }

        return totalNano;
    }



    enum CommandTags {
        TIME_TICK("-t"),            // ✓
        TIME_SECOND("-ts"),         // ✓
        LOG("-log"),                // ✓    No msg
        NEARBY("-nearby"),          //
        SORT("-sort"),              // ✓
        HIGH("-high"),              // ✓
        LOW("-low"),                // ✓
        COMBINE("-combine"),        //
        STOP("-stop");              //

        final static Set<String> tagSet = new HashSet<>();
        static {
            for (CommandTags value : values()) {
                tagSet.add(value.commandTag);
            }
        }

        final String commandTag;

        CommandTags(String commandTag) {
            this.commandTag = commandTag;
        }

        @Nullable
        static CommandTags getTagFromArg(final String tagArg) {
            final String lower = tagArg.toLowerCase(Locale.ROOT);
            for (CommandTags value : values()) {
                if (lower.equals(value.commandTag)) return value;
            }

            return null;
        }
    }
}
