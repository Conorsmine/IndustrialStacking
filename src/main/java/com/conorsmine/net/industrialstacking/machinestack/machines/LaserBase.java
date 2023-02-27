package com.conorsmine.net.industrialstacking.machinestack.machines;

import com.conorsmine.net.industrialstacking.IndustrialStacking;
import com.conorsmine.net.industrialstacking.machinestack.MachineStack;
import com.conorsmine.net.industrialstacking.machinestack.StackableMachines;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTCompoundList;
import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class LaserBase extends MachineStack {

    private ItemStack[] baseInvPrev;
    private boolean itemLock = false;   // boolean used to ensure, that the base only increases output once

    public LaserBase(@NotNull IndustrialStacking plugin, @NotNull Block tileEntity) {
        super(plugin, tileEntity, StackableMachines.LASER_BASE.getMaterial());
        this.baseInvPrev = getBaseItems();
    }

    @Override
    public long getRegularMachinePower() {
        // Doesn't use energy, so ignore it
        return 0;
    }

    @Override
    public void tickMachine() {
        final long currentWork = this.getMachineTile().getLong("currentWork");
        if (currentWork <= 2 || currentWork >= 12) { itemLock = false; return; }
        if (itemLock) return;
        itemLock = true;
        final ItemStack[] baseInvCurrent = getBaseItems();
        addItemToLaserBase(baseInvCurrent);
        baseInvPrev = baseInvCurrent.clone();
    }

    private ItemStack[] getBaseItems() {
        final NBTCompoundList itemList = getNBTItemList();
        final ItemStack[] outItems = new ItemStack[18];
        for (int i = 0; i < itemList.size(); i++) {
            final NBTCompound itemNBT = itemList.get(i);
            outItems[i] = NBTItem.convertNBTtoItem(itemNBT);
        }
        return outItems;
    }

    private void addItemToLaserBase(ItemStack[] baseItems) {
        final ItemStack minedItem = getMinedItem(baseItems);
        if (minedItem == null) return;

        minedItem.setAmount(this.getStackAmount());
        addItemsToInventory(baseItems, minedItem);
        addItemToCompoundList(baseItems);
    }

    private void addItemsToInventory(ItemStack[] baseItems, ItemStack minedItem) {
        for (ItemStack baseItem : baseItems) {
            if (baseItem == null) continue;
            if (baseItem.getDurability() != minedItem.getDurability()) continue;
            if (!baseItem.getType().name().equals(minedItem.getType().name())) continue;
            if (baseItem.getAmount() == baseItem.getMaxStackSize()) continue;

            // Add items to arr
            int addAmount = minedItem.getAmount();
            if ((baseItem.getAmount() + addAmount) > baseItem.getMaxStackSize()) addAmount = (baseItem.getMaxStackSize() - baseItem.getAmount());
            System.out.println("Add: " + addAmount);
            baseItem.setAmount(baseItem.getAmount() + addAmount);
            minedItem.setAmount(minedItem.getAmount() - addAmount);
            System.out.println("Set: " + minedItem.getAmount());

            // Repeat until gone
            if (minedItem.getAmount() > 0)
                addItemsToInventory(baseItems, minedItem);
        }
    }

    private void addItemToCompoundList(final ItemStack[] baseInv) {
        NBTCompoundList baseInvCompoundList = getNBTItemList();

        for (int i = 0; i < baseInv.length; i++) {
            final ItemStack invItem = baseInv[i];
            if (invItem == null || invItem.getType() == Material.AIR) continue;

            final ReadWriteNBT itemNBT = baseInvCompoundList.get(i);
            itemNBT.setInteger("Count", invItem.getAmount());
        }
    }

    private NBTCompoundList getNBTItemList() {
        return getMachineTile().getCompound("outItems").getCompoundList("Items");
    }

    /**
     * @param baseInvCurrent Current laser base inventory
     * @return The currently mined item
     */
    @Nullable
    private ItemStack getMinedItem(final ItemStack[] baseInvCurrent) {
        if (baseInvPrev.length == 0)
            return findFirst(baseInvCurrent);

        final Map<String, OverstackedItem> prevOverstackedItems = getOverstackedItems(baseInvPrev);
        final Map<String, OverstackedItem> currOverstackedItems = getOverstackedItems(baseInvCurrent);

        // New item type was mined
        for (String currKey : currOverstackedItems.keySet()) {
            if (!prevOverstackedItems.containsKey(currKey)) return itemFromOverstack(currOverstackedItems.get(currKey));
        }

        // Existing type was mined
        for (String currKey : currOverstackedItems.keySet()) {
            final OverstackedItem currItem = currOverstackedItems.get(currKey);
            final OverstackedItem prevItem = prevOverstackedItems.get(currKey);

            if (currItem.getAmount() > prevItem.getAmount()) return itemFromOverstack(currItem);
        }

        // This should never happen, should...
        return null;
    }

    /**
     * Overstacking is the act of increasing the items amount over its intended amount.
     * This'll be used to have the amount of all items of one particular type
     * @param baseInvCurrent Itemstack to overstack.
     * @return A map of overstacked items. The key is the items type and damage
     */
    private Map<String, OverstackedItem> getOverstackedItems(final ItemStack[] baseInvCurrent) {
        final Map<String, OverstackedItem> overstackedItemMap = new HashMap<>();
        for (ItemStack item : baseInvCurrent) {
            if (item == null ||item.getType() == Material.AIR) continue;
            final String mapKey = String.format("%s#%d", item.getType().name(), item.getDurability());
            final OverstackedItem ovStackedItem = overstackedItemMap.getOrDefault(mapKey, new OverstackedItem(
                    item.getType(), 0, item.getDurability()));

            ovStackedItem.setAmount(ovStackedItem.getAmount() + item.getAmount());
            overstackedItemMap.put(mapKey, ovStackedItem);
        }

        return overstackedItemMap;
    }

    @Nullable
    private ItemStack findFirst(final ItemStack[] itemArr) {
        for (ItemStack itemStack : itemArr) {
            if (itemStack == null || itemStack.getType() == Material.AIR) continue;
            return itemStack;
        }

        return null;
    }

    private ItemStack itemFromOverstack(final OverstackedItem overstackedItem) {
        return new ItemStack(overstackedItem.getType(), 1, overstackedItem.getDamage());
    }

    private static class OverstackedItem {
        private final Material type;
        private int amount;
        private final short damage;

        public OverstackedItem(Material type, int amount, short damage) {
            this.type = type;
            this.amount = amount;
            this.damage = damage;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            OverstackedItem that = (OverstackedItem) o;
            return amount == that.amount && damage == that.damage && type.name().equals(that.type.name());
        }

        @Override
        public String toString() {
            return "OverstackedItem{" +
                    "type=" + type +
                    ", amount=" + amount +
                    ", damage=" + damage +
                    '}';
        }

        public Material getType() {
            return type;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public short getDamage() {
            return damage;
        }
    }
}
