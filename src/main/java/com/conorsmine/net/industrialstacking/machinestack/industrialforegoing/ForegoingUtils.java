package com.conorsmine.net.industrialstacking.machinestack.industrialforegoing;

import de.tr7zw.nbtapi.NBTCompoundList;
import de.tr7zw.nbtapi.NBTTileEntity;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class ForegoingUtils {



    /**
     * @param machineNBT The {@link NBTTileEntity} of the machine stack.
     * @return A set of upgrades contained in the machine.
     */
    public static Set<ForegoingUpgrades> getUpgradesFromMachine(@NotNull final NBTTileEntity machineNBT) {
        final Set<ForegoingUpgrades> upgradesSet = new HashSet<>();
        if (!machineNBT.hasTag("addonItems")) return upgradesSet;
        final NBTCompoundList upgradeList = machineNBT.getCompound("addonItems").getCompoundList("Items");

        for (ReadWriteNBT upgradeNBT : upgradeList) {
            if (upgradeNBT == null || !upgradeNBT.hasTag("id")) continue;
            String itemId = upgradeNBT.getString("id");

            for (ForegoingUpgrades upgrade : ForegoingUpgrades.values())
                if (upgrade.getUpgradeItemId().equals(itemId)) upgradesSet.add(upgrade);
        }
        return upgradesSet;
    }

    public static long getRegularInputPowerForMachine() {
        // Todo:
        //  Remove hardcoded values and replace with inferred ones via the mods config file.
        return 0;
    }

    /**
     * @param machineNBT The {@link NBTTileEntity} of the machine stack.
     * @return The power an upgraded machine would consume.
     */
    public static long getInputPowerForMachine(@NotNull final NBTTileEntity machineNBT) {
        // Each speed upgrade adds a modifier of 1.5 to the machines input power
        Set<ForegoingUtils.ForegoingUpgrades> upgradesSet = ForegoingUtils.getUpgradesFromMachine(machineNBT);

        boolean hasSpeedOne = upgradesSet.contains(ForegoingUtils.ForegoingUpgrades.SPEED_ONE);
        boolean hasSpeedTwo = upgradesSet.contains(ForegoingUtils.ForegoingUpgrades.SPEED_TWO);
        long power = getRegularInputPowerForMachine();
        if (hasSpeedOne && hasSpeedTwo) power *= 2.25;  // 2,25 => 1.5 * 1.5
        else if (hasSpeedOne) power *= 1.5;

        return power;
    }



    public enum ForegoingUpgrades {
        SPEED_ONE("teslacorelib:speed_tier1"),
        SPEED_TWO("teslacorelib:speed_tier2"),
        ENERGY_ONE("teslacorelib:energy_tier1"),
        ENERGY_TWO("teslacorelib:energy_tier2");

        private final String upgradeItemId;

        ForegoingUpgrades(String upgradeItemId) {
            this.upgradeItemId = upgradeItemId;
        }

        public String getUpgradeItemId() {
            return upgradeItemId;
        }
    }
}
