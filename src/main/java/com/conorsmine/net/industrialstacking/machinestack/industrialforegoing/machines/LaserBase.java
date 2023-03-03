package com.conorsmine.net.industrialstacking.machinestack.industrialforegoing.machines;

import com.conorsmine.net.industrialstacking.IndustrialStacking;
import com.conorsmine.net.industrialstacking.machinestack.MachineStack;
import com.conorsmine.net.industrialstacking.machinestack.StackableMachines;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTCompoundList;
import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class LaserBase extends MachineStack {

    private ItemStack[] baseInvPrev;
    private boolean itemLock = false;   // boolean used to ensure, that the base only increases output once

    public LaserBase(@NotNull IndustrialStacking plugin, @NotNull Block tileEntity) {
        super(plugin, tileEntity, StackableMachines.LASER_BASE);
        this.baseInvPrev = getBaseItems();
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

    /**
     * Adds the mined item to the inventory of the laser base.
     * It'll use the first available identical item or empty slot.
     * @param baseItems Array of items representing the laser base inventory.
     * @param minedItem The item mined from the base and multiplied by the machine stack.
     */
    private void addItemsToInventory(ItemStack[] baseItems, ItemStack minedItem) {
        for (int i = 0; i < baseItems.length; i++) {
            if (minedItem.getAmount() <= 0) return;
            final ItemStack baseItem = baseItems[i];

            // Add to empty slot
            if (baseItem == null) {
                addItemToEmpty(baseItems, i, minedItem);
                shouldRecurs(baseItems, minedItem);
                continue;
            }

            // Add to existing item
            if (!canAdd(baseItem, minedItem)) continue;
            addItemToItem(baseItem, minedItem);
            shouldRecurs(baseItems, minedItem);
        }
    }

    private void shouldRecurs(ItemStack[] baseItems, ItemStack minedItem) {
        if (minedItem.getAmount() > 0)
            addItemsToInventory(baseItems, minedItem);
    }

    /**
     * Adds the amount of the minedItem to the baseItem, until it reaches the {@link ItemStack#getMaxStackSize()}.
     * The {@link ItemStack#getAmount()} of the minedItem represents how much could not be added.
     * @param baseItem Item of the laser base.
     * @param minedItem Item mined by the laser base.
     */
    private void addItemToItem(ItemStack baseItem, ItemStack minedItem) {
        int minedItemAmount = minedItem.getAmount();
        int addAmount = minedItemAmount;
        if ((baseItem.getAmount() + addAmount) > baseItem.getMaxStackSize()) addAmount = (baseItem.getMaxStackSize() - baseItem.getAmount());
        baseItem.setAmount(baseItem.getAmount() + addAmount);
        minedItem.setAmount(minedItemAmount - addAmount);
    }

    /**
     * Adds the minedItem to the empty slot of the laser base, until it reaches the {@link ItemStack#getMaxStackSize()}.
     * The {@link ItemStack#getAmount()} of the minedItem represents how much could not be added.
     * @param baseInv Array of items of the laser base.
     * @param emptyBaseIndex Index of an empty slot.
     * @param minedItem Item mined by the laser base.
     */
    private void addItemToEmpty(ItemStack[] baseInv, int emptyBaseIndex, ItemStack minedItem) {
        int minedItemAmount = minedItem.getAmount();
        int addAmount = minedItemAmount;
        if (minedItem.getAmount() > minedItem.getMaxStackSize()) addAmount = minedItem.getMaxStackSize();
        baseInv[emptyBaseIndex] = minedItem.clone();
        baseInv[emptyBaseIndex].setAmount(addAmount);
        minedItem.setAmount(minedItemAmount - addAmount);
    }

    /**
     * @param baseItem Item of the laser base.
     * @param minedItem Item mined by the laser base.
     * @return True, if both are the same and the baseItem isn't at its max stack size.
     */
    private boolean canAdd(ItemStack baseItem, ItemStack minedItem) {
        if (baseItem.getDurability() != minedItem.getDurability()) return false;
        if (!baseItem.getType().name().equals(minedItem.getType().name())) return false;
        if (baseItem.getAmount() == baseItem.getMaxStackSize()) return false;
        return true;
    }

    /**
     * @param baseInv Adds the calculated inventory of the laser base to the NBT of its {@link de.tr7zw.nbtapi.NBTTileEntity}.
     */
    private void addItemToCompoundList(final ItemStack[] baseInv) {
        NBTCompoundList originalBaseInvCompoundList = getNBTItemList();
        NBTCompound[] modifiedCompounds = new NBTCompound[baseInv.length];
        for (int i = 0; i < originalBaseInvCompoundList.size(); i++)
            modifiedCompounds[i] = new NBTContainer(originalBaseInvCompoundList.get(i).toString());
        originalBaseInvCompoundList.clear();

        for (int i = 0; i < baseInv.length; i++) {
            final ItemStack invItem = baseInv[i];
            if (invItem == null || invItem.getType() == Material.AIR) continue;

            // Note to future me:
            // The "Slot" tag is VERY important if you want to add an item to an array!
            NBTContainer itemNBT = NBTItem.convertItemtoNBT(invItem);
            itemNBT.setInteger("Slot", i);
            modifiedCompounds[i] = itemNBT;
        }

        for (NBTCompound modifiedCompound : modifiedCompounds) {
            if (modifiedCompound == null || !modifiedCompound.hasTag("id")) continue;
            originalBaseInvCompoundList.addCompound(modifiedCompound);
        }
    }

    /**
     * @return The {@link NBTCompoundList} representing the laser bases inventory.
     */
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
