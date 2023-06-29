package com.conorsmine.net.industrialstacking.machinestack.industrialforegoing;

import com.conorsmine.net.industrialstacking.IndustrialStacking;
import com.conorsmine.net.industrialstacking.machinestack.MachineStack;
import com.conorsmine.net.industrialstacking.machinestack.StackableMachines;
import com.conorsmine.net.industrialstacking.modconfigs.industrialforegoing.ForegoingConfigData;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTCompoundList;
import de.tr7zw.nbtapi.NBTTileEntity;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ForegoingUtils {

    /**
     * @param machineNBT The {@link NBTTileEntity} of the machine stack.
     * @return The amount of power currently stored in the machine.
     */
    public static long getCurrentMachinePower(@NotNull final NBTTileEntity machineNBT) {
        final NBTCompound workEnergy = machineNBT.getCompound("work_energy");
        if (workEnergy == null) return 0L;
        final Long teslaPower = workEnergy.getLong("TeslaPower");
        return (teslaPower == null) ? 0L : teslaPower;
    }

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

    /**
     * @param pl {@link IndustrialStacking} for retrieving {@link com.conorsmine.net.industrialstacking.modconfigs.ModConfigManager}.
     * @param machine {@link StackableMachines} enum.
     * @return Amount of power as set in the mods config file.
     */
    public static int getRegularInputPowerForMachine(final IndustrialStacking pl, final StackableMachines machine) {
        ForegoingConfigData foregoingConfigData = pl.getModConfigManager().getForegoingConfig().get(machine);
        if (foregoingConfigData == null) return 0;

        Map<String, Object> configData = foregoingConfigData.getConfigData();
        return (int) configData.getOrDefault("energyRate", 0);
    }

    /**
     * @param machineStack The {@link MachineStack} of the machine.
     * @return The power an upgraded machine would consume.
     */
    public static long getInputPowerForMachine(@NotNull final MachineStack machineStack) {
        // Each speed upgrade adds a modifier of 1.5 to the machines input power
        Set<ForegoingUtils.ForegoingUpgrades> upgradesSet = ForegoingUtils.getUpgradesFromMachine(machineStack.getMachineTile());

        boolean hasSpeedOne = upgradesSet.contains(ForegoingUtils.ForegoingUpgrades.SPEED_ONE);
        boolean hasSpeedTwo = upgradesSet.contains(ForegoingUtils.ForegoingUpgrades.SPEED_TWO);
        long power = getRegularInputPowerForMachine(machineStack.getPl(), machineStack.getMachineEnum());
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
