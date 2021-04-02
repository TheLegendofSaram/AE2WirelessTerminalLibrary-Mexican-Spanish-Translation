package de.mari_023.fabric.ae2wtlib.terminal;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.FixedItemInv;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import de.mari_023.fabric.ae2wtlib.wct.ItemMagnetCard;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;

public class FixedWTInv implements FixedItemInv {

    public static final int OFFHAND = 4;
    public static final int TRASH = 5;
    public static final int INFINITY_BOOSTER_CARD = 6;
    public static final int MAGNET_CARD = 7;


    private final PlayerInventory playerInventory;
    private final ItemStack wt;

    private static final int slotOffset = 36;
    private static final int offHandSlot = 40;

    public FixedWTInv(PlayerInventory playerInventory, ItemStack wt) {
        this.playerInventory = playerInventory;
        this.wt = wt;
    }

    @Override
    public int getSlotCount() {
        return 8;
    }

    @Override
    public ItemStack getInvStack(int i) {
        if(i < 4 && i >= 0) {
            return playerInventory.getStack(i + slotOffset);
        } else if(i == OFFHAND) return playerInventory.getStack(offHandSlot);
        else if(i == TRASH && wt.getItem() instanceof ItemWT)
            return ((ItemWT) wt.getItem()).getSavedSlot(wt, "trash");
        else if(i == INFINITY_BOOSTER_CARD && wt.getItem() instanceof IInfinityBoosterCardHolder)
            return ((IInfinityBoosterCardHolder) wt.getItem()).getBoosterCard(wt);
        else if(i == MAGNET_CARD && wt.getItem() instanceof ItemWT)
            return ((ItemWT) wt.getItem()).getSavedSlot(wt, "magnetCard");
        return null;
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemStack) {
        if(i == 0) {
            return playerInventory.isValid(slotOffset, itemStack) && itemStack.getItem() instanceof ArmorItem && ((ArmorItem) itemStack.getItem()).getSlotType().equals(EquipmentSlot.FEET);
        } else if(i == 1) {
            return playerInventory.isValid(slotOffset + 1, itemStack) && itemStack.getItem() instanceof ArmorItem && ((ArmorItem) itemStack.getItem()).getSlotType().equals(EquipmentSlot.LEGS);
        } else if(i == 2) {
            return playerInventory.isValid(slotOffset + 2, itemStack) && itemStack.getItem() instanceof ArmorItem && ((ArmorItem) itemStack.getItem()).getSlotType().equals(EquipmentSlot.CHEST);
        } else if(i == 3) {
            return playerInventory.isValid(slotOffset + 3, itemStack) && itemStack.getItem() instanceof ArmorItem && ((ArmorItem) itemStack.getItem()).getSlotType().equals(EquipmentSlot.HEAD);
        } else if(i == OFFHAND) return playerInventory.isValid(offHandSlot, itemStack);
        else if(i == TRASH) return true;
        else if(i == INFINITY_BOOSTER_CARD)
            return itemStack.getItem() instanceof ItemInfinityBooster || itemStack.isEmpty();
        else if(i == MAGNET_CARD)
            return itemStack.getItem() instanceof ItemMagnetCard || itemStack.isEmpty();
        return false;
    }

    @Override
    public boolean setInvStack(int i, ItemStack itemStack, Simulation simulation) {
        if(i < 4 && i >= 0) {
            if(simulation.isAction()) playerInventory.setStack(i + slotOffset, itemStack);
            return true;
        } else if(i == OFFHAND) {
            if(simulation.isAction()) playerInventory.setStack(offHandSlot, itemStack);
            return true;
        } else if(i == TRASH) {
            if(simulation.isAction()) ((ItemWT) wt.getItem()).setSavedSlot(wt, itemStack, "trash");
            return true;
        } else if(i == INFINITY_BOOSTER_CARD) {
            if(!(itemStack.getItem() instanceof ItemInfinityBooster) && !itemStack.equals(ItemStack.EMPTY))
                return false;
            if(simulation.isAction()) {
                ((IInfinityBoosterCardHolder) wt.getItem()).setBoosterCard(wt, itemStack);
            }
            return true;
        } else if(i == MAGNET_CARD) {
            if(!(itemStack.getItem() instanceof ItemMagnetCard) && !itemStack.equals(ItemStack.EMPTY)) return false;
            if(simulation.isAction())
                ((ItemWT) wt.getItem()).setSavedSlot(wt, itemStack, "magnetCard");
            return true;
        }
        return false;
    }

    @Override
    public ItemStack extractStack(int slot, ItemFilter filter, ItemStack mergeWith, int maxCount, Simulation simulation) {
        if(slot == INFINITY_BOOSTER_CARD) {
            ItemStack boosterCard = ((IInfinityBoosterCardHolder) wt.getItem()).getBoosterCard(wt);
            if(simulation.isAction()) ((IInfinityBoosterCardHolder) wt.getItem()).setBoosterCard(wt, ItemStack.EMPTY);
            return boosterCard;
        } else if(slot == MAGNET_CARD) {
            ItemStack magnetCard = ((ItemWT) wt.getItem()).getSavedSlot(wt, "magnetCard");
            if(simulation.isAction())
                ((ItemWT) wt.getItem()).setSavedSlot(wt, ItemStack.EMPTY, "magnetCard");
            return magnetCard;
        }
        return FixedItemInv.super.extractStack(slot, filter, mergeWith, maxCount, simulation);
    }
}