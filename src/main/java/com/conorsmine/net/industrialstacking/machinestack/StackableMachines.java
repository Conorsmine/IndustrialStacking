package com.conorsmine.net.industrialstacking.machinestack;

import com.conorsmine.net.industrialstacking.IndustrialStacking;
import com.conorsmine.net.industrialstacking.machinestack.industrialforegoing.machines.*;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public enum StackableMachines {

    LASER_DRILL                 (Material.valueOf("INDUSTRIALFOREGOING_LASER_DRILL"), LaserDrill.class, "laser_drill"),
    LASER_BASE                  (Material.valueOf("INDUSTRIALFOREGOING_LASER_BASE"), LaserBase.class, "laser_base"),
    HYDRATOR                    (Material.valueOf("INDUSTRIALFOREGOING_HYDRATOR"), Hydrator.class, "hydrator"),
    VILLAGER_TRADE_EXCHANGER    (Material.valueOf("INDUSTRIALFOREGOING_VILLAGER_TRADE_EXCHANGER"), VillagerTradeExchanger.class, "villager_trade_exchanger"),
    RESOURCEFUL_FURNACE         (Material.valueOf("INDUSTRIALFOREGOING_RESOURCEFUL_FURNACE"), ResourcefulFurnace.class, "resourceful_furnace"),
    MATERIAL_STONEWORK_FACTORY  (Material.valueOf("INDUSTRIALFOREGOING_MATERIAL_STONEWORK_FACTORY"), MaterialStoneworkFactory.class, "material_stonework_factory"),
    ANIMAL_SEWER                (Material.valueOf("INDUSTRIALFOREGOING_ANIMAL_BYPRODUCT_RECOLECTOR"), AnimalSewer.class, "animal_byproduct_recolector"),
    TREE_FLUID_EXTRACTOR        (Material.valueOf("INDUSTRIALFOREGOING_TREE_FLUID_EXTRACTOR"), TreeFluidExtractor.class, "tree_fluid_extractor"),
    MOB_DUPLICATOR              (Material.valueOf("INDUSTRIALFOREGOING_MOB_DUPLICATOR"), MobDuplicator.class, "mob_duplicator"),
    RESOURCE_FISHER             (Material.valueOf("INDUSTRIALFOREGOING_WATER_RESOURCES_COLLECTOR"), ResourceFisher.class, "water_resources_collector"),
    POTION_BREWER               (Material.valueOf("INDUSTRIALFOREGOING_POTION_ENERVATOR"), PotionBrewer.class, "potion_enervator"),
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
