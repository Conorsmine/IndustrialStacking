package com.conorsmine.net.industrialstacking.machinestack.compactvoidminer;

import com.conorsmine.net.industrialstacking.IndustrialStacking;
import com.conorsmine.net.industrialstacking.machinestack.StackableMachines;
import com.conorsmine.net.industrialstacking.modconfigs.compactvoidminer.VoidMinerConfigData;
import de.tr7zw.nbtapi.NBTCompoundList;
import de.tr7zw.nbtapi.NBTTileEntity;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CompactMinerUtils {

    /**
     * @param machineNBT The {@link NBTTileEntity} of the machine stack.
     * @return A map of upgrades and their amount.
     */
    public static Map<MinerUpgrades, Integer> getUpgradesFromMachine(@NotNull final NBTTileEntity machineNBT) {
        final Map<MinerUpgrades, Integer> upgradeMap = new HashMap<>();
        if (!machineNBT.hasTag("upgradeInventory")) return upgradeMap;
        final NBTCompoundList upgradeList = machineNBT.getCompound("upgradeInventory").getCompoundList("Items");

        for (ReadWriteNBT upgradeNBT : upgradeList) {
            if (upgradeNBT == null || !upgradeNBT.hasTag("id")) continue;
            final String itemId = upgradeNBT.getString("id");
            final byte itemAmount = upgradeNBT.getByte("Count");

            for (MinerUpgrades upgrade : MinerUpgrades.values())
                if (upgrade.getUpgradeItemId().equals(itemId))
                    upgradeMap.put(upgrade, (upgradeMap.getOrDefault(upgrade, 0) + itemAmount));
        }
        return upgradeMap;
    }

    /**
     * @param pl {@link IndustrialStacking} for retrieving {@link com.conorsmine.net.industrialstacking.modconfigs.ModConfigManager}.
     * @return Amount of power as set in the mods config file.
     */
    public static int getRegularInputPowerForMachine(@NotNull final IndustrialStacking pl) {
        VoidMinerConfigData voidMinerConfigData = pl.getModConfigManager().getVoidMinerConfig().get(StackableMachines.COMPACT_VOID_MINER);
        if (voidMinerConfigData == null) return 1024;

        Map<String, Object> configData = voidMinerConfigData.getConfigData();
        return (int) configData.getOrDefault("\"BaseMachineEnergyUsage\"", 1024);
    }

    /**
     * @param pl {@link IndustrialStacking} for retrieving {@link com.conorsmine.net.industrialstacking.modconfigs.ModConfigManager}.
     * @return Power cost increase per speed upgrade.
     */
    public static int getEnergyScaleFactor(@NotNull final IndustrialStacking pl) {
        VoidMinerConfigData voidMinerConfigData = pl.getModConfigManager().getVoidMinerConfig().get(StackableMachines.COMPACT_VOID_MINER);
        if (voidMinerConfigData == null) return 2;

        Map<String, Object> configData = voidMinerConfigData.getConfigData();
        return (int) configData.getOrDefault("\"MachineEnergyUsageUpgradeScale\"", 2);
    }

    /**
     * @param pl {@link IndustrialStacking} for retrieving {@link com.conorsmine.net.industrialstacking.modconfigs.ModConfigManager}.
     * @return The power an upgraded machine would consume.
     */
    public static long getInputPowerPerMachine(@NotNull final IndustrialStacking pl, @NotNull final NBTTileEntity machineNBT) {
        // Function for calculating the required energy, where:
        // g: Base Machine Energy Usage => #getRegularInputPowerForMachine
        // m: Energy scale factor => #getEnergyScaleFactor
        // e: Amount of energy upgrades
        // s: ⌊(speedUpgrades / 4)⌋
        // Energy(g, m, s, e) = (g * m^s) / (m^e)

        final Map<MinerUpgrades, Integer> machineUpgrades = getUpgradesFromMachine(machineNBT);

        final int regularPower = getRegularInputPowerForMachine(pl);                                                             // Our g
        final int scaleFactor = getEnergyScaleFactor(pl);                                                                        // Our m
        final int upgradeSpeedInfluence = Math.floorDiv(machineUpgrades.getOrDefault(MinerUpgrades.SPEED, 0), 4);     // Our s
        final int upgradeEnergyInfluence = machineUpgrades.getOrDefault(MinerUpgrades.ENERGY, 0);                     // Our e

        long energyOutput = (long) (regularPower * Math.pow(scaleFactor, upgradeSpeedInfluence));
        if (upgradeEnergyInfluence > 0) energyOutput /= Math.pow(scaleFactor, upgradeEnergyInfluence);
        return Math.max(energyOutput, 1);   // Clamp to 1
    }



    public enum MinerUpgrades {
        SPEED("mekanism:speedupgrade"),
        ENERGY("mekanism:energyupgrade"),
        FILTER("mekanism:filterupgrade"),
        MUFFLER("mekanism:mufflingupgrade"),
        ANCHOR("mekanism:anchorupgrade"),
        GAS("mekanism:gasupgrade"),
        UNDEFINED("");  // For anything else

        private final String upgradeItemId;

        MinerUpgrades(String upgradeItemId) {
            this.upgradeItemId = upgradeItemId;
        }

        public String getUpgradeItemId() {
            return upgradeItemId;
        }
    }
}
