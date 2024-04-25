package de.mari_023.ae2wtlib.wut;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import top.theillusivec4.curios.api.SlotContext;

import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.api.upgrades.Upgrades;
import appeng.api.util.IConfigManager;
import appeng.core.definitions.AEItems;
import appeng.menu.locator.ItemMenuHostLocator;

import de.mari_023.ae2wtlib.TextConstants;
import de.mari_023.ae2wtlib.terminal.ItemWT;

public class ItemWUT extends ItemWT {
    @Override
    public InteractionResultHolder<ItemStack> use(final Level level, final Player player, final InteractionHand hand) {
        if (WUTHandler.getCurrentTerminal(player.getItemInHand(hand)).isEmpty()) {
            if (!level.isClientSide())
                player.sendSystemMessage(TextConstants.TERMINAL_EMPTY);
            return new InteractionResultHolder<>(InteractionResult.sidedSuccess(level.isClientSide()),
                    player.getItemInHand(hand));
        }
        /*
         * if(player.isShiftKeyDown()) { if(w.isClientSide()) Minecraft.getInstance().setScreen(new
         * WUTSelectScreen(player.getItemInHand(hand))); return new InteractionResultHolder<>(InteractionResult.SUCCESS,
         * player.getItemInHand(hand)); } else
         */
        return super.use(level, player, hand);
    }

    @Override
    public double getChargeRate(ItemStack stack) {
        return 800d
                * (countInstalledTerminals(stack) + 1 + getUpgrades(stack).getInstalledUpgrades(AEItems.ENERGY_CARD));
    }

    @Override
    public boolean open(final Player player, final ItemMenuHostLocator locator,
            boolean returningFromSubmenu) {
        return WUTHandler.open(player, locator, returningFromSubmenu);
    }

    @Override
    public MenuType<?> getMenuType(ItemMenuHostLocator locator, Player player) {
        return WUTHandler.wirelessTerminals.get(WUTHandler.getCurrentTerminal(locator.locateItem(player))).menuType();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(final ItemStack stack, final TooltipContext context, final List<Component> lines,
            final TooltipFlag advancedTooltips) {
        lines.add(TextConstants.UNIVERSAL);
        for (var terminal : WUTHandler.wirelessTerminals.entrySet()) {
            if (WUTHandler.hasTerminal(stack, terminal.getKey()))
                lines.add(terminal.getValue().terminalName());
        }
        super.appendHoverText(stack, context, lines, advancedTooltips);
    }

    @Override
    public IUpgradeInventory getUpgrades(ItemStack stack) {
        return UpgradeInventories.forItem(stack, countInstalledTerminals(stack) * 2, this::onUpgradesChanged);
    }

    public void onUpgradesChanged(ItemStack stack, IUpgradeInventory upgrades) {
        setAEMaxPowerMultiplier(stack,
                countInstalledTerminals(stack) + Upgrades.getEnergyCardMultiplier(upgrades));
    }

    public int countInstalledTerminals(ItemStack stack) {
        int terminals = 0;
        for (String s : WUTHandler.terminalNames) {
            if (WUTHandler.hasTerminal(stack, s))
                terminals++;
        }
        return terminals;
    }

    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int i, boolean bl) {
        for (var terminal : WUTHandler.wirelessTerminals.entrySet()) {
            if (!WUTHandler.hasTerminal(itemStack, terminal.getKey()))
                continue;
            terminal.getValue().item().inventoryTick(itemStack,
                    level, entity, i, bl);
        }
    }

    public IConfigManager getConfigManager(Supplier<ItemStack> target) {// FIXME potentially reuse the config manager?
        return WUTHandler.wirelessTerminals.get(WUTHandler.getCurrentTerminal(target.get())).item()
                .getConfigManager(target);
    }

    public void curioTick(SlotContext slotContext, ItemStack stack) {
        inventoryTick(stack, slotContext.entity().level(), slotContext.entity(), 0, false);
    }
}
