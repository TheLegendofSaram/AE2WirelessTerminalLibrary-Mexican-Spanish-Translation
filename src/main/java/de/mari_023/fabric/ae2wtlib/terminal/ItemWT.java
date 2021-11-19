package de.mari_023.fabric.ae2wtlib.terminal;

import appeng.core.AEConfig;
import appeng.items.tools.powered.WirelessTerminalItem;
import appeng.menu.MenuLocator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public abstract class ItemWT extends WirelessTerminalItem implements IUniversalWirelessTerminalItem {

    public ItemWT(Item.Settings props) {
        super(AEConfig.instance().getWirelessTerminalBattery(), props);
    }

    @Override
    public TypedActionResult<ItemStack> use(final World w, final PlayerEntity player, final Hand hand) {
        var is = player.getStackInHand(hand);
        if(canOpen(is, player)) {
            open(player, MenuLocator.forHand(player, hand));
            return new TypedActionResult<>(ActionResult.SUCCESS, is);
        }
        return new TypedActionResult<>(ActionResult.FAIL, is);
    }

    @Override
    public boolean checkPreconditions(ItemStack item, PlayerEntity player) {
        return super.checkPreconditions(item, player);
    }

    /**
     * get a previously stored {@link ItemStack} from a WirelessTerminal
     *
     * @param hostItem the Terminal to load from
     * @param slot     the location where the item is stored
     * @return the stored Item or {@link ItemStack}.EMPTY if it wasn't found
     */
    public static ItemStack getSavedSlot(ItemStack hostItem, String slot) {
        if(!(hostItem.getItem() instanceof IUniversalWirelessTerminalItem)) return ItemStack.EMPTY;
        return ItemStack.fromNbt(hostItem.getOrCreateNbt().getCompound(slot));
    }

    /**
     * store an {@link ItemStack} in a WirelessTerminal
     * this will overwrite any previously existing tags in slot
     *
     * @param hostItem  the Terminal to store in
     * @param savedItem the item to store
     * @param slot      the location where the stored item will be
     */
    public static void setSavedSlot(ItemStack hostItem, ItemStack savedItem, String slot) {
        if(!(hostItem.getItem() instanceof IUniversalWirelessTerminalItem)) return;
        NbtCompound wctTag = hostItem.getOrCreateNbt();
        if(savedItem.isEmpty()) wctTag.remove(slot);
        else wctTag.put(slot, savedItem.writeNbt(new NbtCompound()));
    }

    /**
     * get a previously stored boolean from a WirelessTerminal
     *
     * @param hostItem the Terminal to load from
     * @return the boolean or false if it wasn't found
     */
    public static boolean getBoolean(ItemStack hostItem, String key) {
        if(!(hostItem.getItem() instanceof IUniversalWirelessTerminalItem)) return false;
        return hostItem.getOrCreateNbt().getBoolean(key);
    }

    /**
     * store a boolean in a WirelessTerminal
     * this will overwrite any previously existing tags in slot
     *
     * @param hostItem the Terminal to store in
     * @param b        the boolean to store
     * @param key      the location where the stored item will be
     */
    public static void setBoolean(ItemStack hostItem, boolean b, String key) {
        if(!(hostItem.getItem() instanceof IUniversalWirelessTerminalItem)) return;
        NbtCompound wctTag = hostItem.getOrCreateNbt();
        wctTag.putBoolean(key, b);
    }
}