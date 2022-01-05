package de.mari_023.fabric.ae2wtlib.trinket;

import appeng.api.inventories.InternalInventory;
import dev.emi.trinkets.api.TrinketInventory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@Deprecated
public class CombinedTrinketInventory implements InternalInventory {

    private final Map<String, Map<String, TrinketInventory>> inventory;

    public CombinedTrinketInventory(Map<String, Map<String, TrinketInventory>> trinketInventory) {
        inventory = trinketInventory;
    }

    @Override
    public int size() {
        int currentIndex = 0;
        for(Map.Entry<String, Map<String, TrinketInventory>> group : inventory.entrySet()) {
            for(Map.Entry<String, TrinketInventory> slot : group.getValue().entrySet()) {
                currentIndex += slot.getValue().getContainerSize();
            }
        }
        return currentIndex;
    }

    @Nullable
    public TrinketLocator getLocator(int index) {//TODO move to a search function in TrinketsHelper
        int currentIndex = 0;
        for(Map.Entry<String, Map<String, TrinketInventory>> group : inventory.entrySet()) {
            for(Map.Entry<String, TrinketInventory> slotType : group.getValue().entrySet()) {
                if(index >= currentIndex + slotType.getValue().getContainerSize()) {
                    currentIndex += slotType.getValue().getContainerSize();
                } else {
                    return new TrinketLocator(group.getKey(), slotType.getKey(), index - currentIndex);
                }
            }
        }
        return null;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        int currentIndex = 0;
        for(Map.Entry<String, Map<String, TrinketInventory>> group : inventory.entrySet()) {
            for(Map.Entry<String, TrinketInventory> slot : group.getValue().entrySet()) {
                if(index >= currentIndex + slot.getValue().getContainerSize()) {
                    currentIndex += slot.getValue().getContainerSize();
                } else {
                    return slot.getValue().getItem(index - currentIndex);
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isItemValid(int index, ItemStack stack) {
        int currentIndex = 0;
        for(Map.Entry<String, Map<String, TrinketInventory>> group : inventory.entrySet()) {
            for(Map.Entry<String, TrinketInventory> slot : group.getValue().entrySet()) {
                if(index >= currentIndex + slot.getValue().getContainerSize()) {
                    currentIndex += slot.getValue().getContainerSize();
                } else {
                    return slot.getValue().canPlaceItem(index - currentIndex, stack);
                }
            }
        }
        return false;
    }

    @Override
    public void setItemDirect(int index, @NotNull ItemStack stack) {
        int currentIndex = 0;
        for(Map.Entry<String, Map<String, TrinketInventory>> group : inventory.entrySet()) {
            for(Map.Entry<String, TrinketInventory> slot : group.getValue().entrySet()) {
                if(index >= currentIndex + slot.getValue().getContainerSize()) {
                    currentIndex += slot.getValue().getContainerSize();
                } else {
                    slot.getValue().setItem(index - currentIndex, stack);
                    return;
                }
            }
        }
    }
}