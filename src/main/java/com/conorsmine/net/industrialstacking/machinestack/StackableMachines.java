package com.conorsmine.net.industrialstacking.machinestack;

import com.conorsmine.net.industrialstacking.IndustrialStacking;
import com.conorsmine.net.industrialstacking.machinestack.industrialforegoing.machines.LaserBase;
import com.conorsmine.net.industrialstacking.machinestack.industrialforegoing.machines.LaserDrill;
import com.conorsmine.net.industrialstacking.machinestack.industrialforegoing.machines.MobDuplicator;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public enum StackableMachines {

    LASER_DRILL     (Material.valueOf("INDUSTRIALFOREGOING_LASER_DRILL"), LaserDrill.class, "laser_drill"),
    LASER_BASE      (Material.valueOf("INDUSTRIALFOREGOING_LASER_BASE"), LaserBase.class, "laser_base"),
    MOB_DUPLICATOR  (Material.valueOf("INDUSTRIALFOREGOING_MOB_DUPLICATOR"), MobDuplicator.class, "mob_duplicator")
    ;



    private static final Map<Material, StackableMachines> matMap = new HashMap<>();
    private static final Set<String> matNameSet = new HashSet<>();  // A set to quickly check if a material is registered
    static {
        for (StackableMachines stackableMachine : values()) {
            matMap.put(stackableMachine.material, stackableMachine);
            matNameSet.add(stackableMachine.material.name().toUpperCase(Locale.ROOT));
        }
    }

    /**
     * This value isn't only for the "config.yml" file of the plugin, but also for the config file of the mod itself.
     */
    private final String configName;
    private final Material material;
    private final Class<? extends MachineStack> clazz;

    /**
     * @param material {@link org.bukkit.Material}
     * @param clazz Class of the stackable machine
     * @param configName Key identifying {@link org.bukkit.configuration.ConfigurationSection}
     */
    StackableMachines(Material material, Class<? extends MachineStack> clazz, String configName) {
        this.material = material;
        this.clazz = clazz;
        this.configName = configName;
    }

    @Nullable
    public MachineStack createNew(IndustrialStacking plugin, Block machineBlock) {
        MachineStack machineStack = null;
        try {
            machineStack = clazz.getConstructor(IndustrialStacking.class, Block.class).newInstance(plugin, machineBlock);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }


        return machineStack;
    }

    public Material getMaterial() {
        return material;
    }

    public Class<? extends MachineStack> getClazz() {
        return clazz;
    }

    public String getConfigName() {
        return configName;
    }

    @Nullable
    public static StackableMachines machineFromType(Material material) {
        return matMap.get(material);
    }

    @Nullable
    public static StackableMachines machineFromName(String matName) {
        if (!matNameSet.contains(matName.toUpperCase(Locale.ROOT))) return null;

        for (StackableMachines value : values()) {
            if (value.getMaterial().name().equals(matName)) return value;
        }
        return null;
    }

    public static Set<String> getMatNameSet() {
        return matNameSet;
    }
}
