package com.conorsmine.net.industrialstacking;

import com.conorsmine.net.industrialstacking.machinestack.MachineStack;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class StackProfiler {

    private final IndustrialStacking pl;
    private final long tickDuration;
    private long ticksPassed = 0;
    private final Map<MachineStack, List<Long>> nanoTimePerMachine = new HashMap<>();
    private final CompletableFuture<ProfilerResult> completableResult = new CompletableFuture<>();

    public StackProfiler(IndustrialStacking pl, long tickDuration) {
        this.pl = pl;
        this.tickDuration = tickDuration;
    }

    public CompletableFuture<ProfilerResult> runProfiler() {
        pl.getStackManager().setProfiler(this);
        return completableResult;
    }

    public void addNanoTime(MachineStack machine, long nanoTime) {
        final List<Long> machineTimeList = nanoTimePerMachine.getOrDefault(machine, new LinkedList<>());
        machineTimeList.add(nanoTime);
        nanoTimePerMachine.put(machine, machineTimeList);
    }

    public boolean tick() {
        ticksPassed++;

        boolean isFinished = (ticksPassed >= tickDuration);
        if (isFinished) completableResult.complete(createResult());
        return isFinished;
    }

    public ProfilerResult createResult() {
        return new ProfilerResult(pl, tickDuration, nanoTimePerMachine);
    }

    public long getTickDuration() {
        return tickDuration;
    }



    public static class ProfilerResult {

        private final IndustrialStacking pl;
        private final long tickDuration;
        private final Map<MachineStack, List<Long>> nanoTimePerMachine;

        public ProfilerResult(IndustrialStacking pl, long tickDuration, Map<MachineStack, List<Long>> nanoTimePerMachine) {
            this.pl = pl;
            this.tickDuration = tickDuration;
            this.nanoTimePerMachine = nanoTimePerMachine;
        }

        public Map<MachineStack, List<Long>> getNanoTimePerMachine() {
            return nanoTimePerMachine;
        }

        public long getTickDuration() {
            return tickDuration;
        }

        //
//        @Nullable
//        public Tuple<MachineStack, Long> getLaggiestMachine() {
//            //noinspection ComparatorMethodParameterNotUsed
//            final List<Map.Entry<MachineStack, Long>> sortedMachineTimes = sumNanoTimePerMachine().entrySet().stream()
//                    .sorted((o1, o2) -> (o1.getValue() > o2.getValue()) ? 1 : -1)
//                    .collect(Collectors.toList());
//
//            if (sortedMachineTimes.size() <= 0) return null;
//            return new Tuple<>(sortedMachineTimes.get(0).getKey(), sortedMachineTimes.get(0).getValue());
//        }
//
//        public HashMap<MachineStack, Long> sumNanoTimePerMachine() {
//            return nanoTimePerMachine.entrySet().stream()
//                    .map(k -> new HashMap<MachineStack, Long>() {{
//                        put(k.getKey(), k.getValue().stream().reduce(0L, Long::sum));
//                    }})
//                    .collect(Collectors.toList()).get(0);
//        }
//
//        private long getNanoTimeAbsolute() {
//            final AtomicLong absoluteNano = new AtomicLong(0);
//            nanoTimePerMachine.values().forEach(s -> s.forEach(absoluteNano::addAndGet));
//            return absoluteNano.get();
//        }
//
//        private long getNanoTimePerTick() {
//            return (getNanoTimeAbsolute() / tickDuration);
//        }
//
//        private double nanoTimeToMillis(long nanoTime) {
//            return ((double) nanoTime) / 1_000_000;
//        }
    }
}
