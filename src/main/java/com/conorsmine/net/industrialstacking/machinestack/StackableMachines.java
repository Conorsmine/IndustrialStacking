package com.conorsmine.net.industrialstacking.machinestack;

import com.conorsmine.net.industrialstacking.IndustrialStacking;
import com.conorsmine.net.industrialstacking.exceptions.MachineMapException;
import com.conorsmine.net.industrialstacking.machinestack.compactvoidminer.CompactVoidMiner;
import com.conorsmine.net.industrialstacking.machinestack.industrialforegoing.machines.*;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public enum StackableMachines {

    // Industrial Foregoing
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

    // Compact void miner
    COMPACT_VOID_MINER          (Material.valueOf("COMPACTVOIDMINERS_VOID_MINER"), CompactVoidMiner.class, "void miner")
    ;



    private static final Map<Material, StackableMachines> matMap = new HashMap<>();
    private static final Map<StackableMods, StackableMachines[]> modMachines = new HashMap<>();
    private static final Set<String> matNameSet = new HashSet<>();  // A set to quickly check if a material is registered
    static {
        for (StackableMachines stackableMachine : values()) {
            matMap.put(stackableMachine.material, stackableMachine);
            matNameSet.add(stackableMachine.material.name().toUpperCase(Locale.ROOT));
        }

        modMachines.put(StackableMods.INDUSTRIAL_FOREGOING, new StackableMachines[]
                {LASER_DRILL, LASER_BASE, HYDRATOR, VILLAGER_TRADE_EXCHANGER, RESOURCEFUL_FURNACE,
                        MATERIAL_STONEWORK_FACTORY, ANIMAL_SEWER, TREE_FLUID_EXTRACTOR, MOB_DUPLICATOR,
                        RESOURCE_FISHER, POTION_BREWER });

        modMachines.put(StackableMods.COMPACT_VOID_MINER, new StackableMachines[] { COMPACT_VOID_MINER });
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

    /**
     * @param plugin Instance of the plugin for parsing to the constructor
     * @param machineBlock Block of the machine
     * @return New instance of a {@link MachineStack} or null if it failed.
     */
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

    /**
     * @return Material of the machine
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * @return String corresponding to that in the "config.yml" file of the plugin and of the mods config
     */
    public String getConfigName() {
        return configName;
    }

    /**
     * @return The {@link StackableMods} for this machine.
     */
    public StackableMods getModFromMachine() {
        for (Map.Entry<StackableMods, StackableMachines[]> modEntry : modMachines.entrySet()) {
            for (StackableMachines machine : modEntry.getValue()) {
                if (machine == this) return modEntry.getKey();
            }
        }

        throw new MachineMapException(String.format("\"%s\" is not mapped to a StackableMod enum!", this.name()));
    }

    /**
     * @param material Material of the machine
     * @return The corresponding machine or null if no machine has that material
     */
    @Nullable
    public static StackableMachines machineFromType(Material material) {
        return matMap.get(material);
    }

    /**
     * While the string is automatically converted to upper case, the position of special characters like underscores have to be added, if need be.
     * @param matName Name of the material
     * @return The corresponding machine or null if no machine has that material name
     */
    @Nullable
    public static StackableMachines machineFromName(String matName) {
        if (!matNameSet.contains(matName.toUpperCase(Locale.ROOT))) return null;

        for (StackableMachines value : values()) {
            if (value.getMaterial().name().equals(matName)) return value;
        }
        return null;
    }

    /**
     * Meant for quickly checking if a material is viable for stacking.
     * @return Set of material names
     */
    public static Set<String> getMatNameSet() {
        return matNameSet;
    }

    /**
     * @return Map of {@link StackableMachines} array for {@link StackableMods}.
     */
    public static Map<StackableMods, StackableMachines[]> getModMachines() {
        return modMachines;
    }

    /**
     * @return The {@link StackableMods} for this machine.
     */
    public static StackableMods getModFromMachine(StackableMachines machine) {
        return machine.getModFromMachine();
    }
}
