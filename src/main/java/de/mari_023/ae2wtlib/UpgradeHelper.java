package de.mari_023.ae2wtlib;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.world.level.ItemLike;

import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;
import appeng.core.localization.GuiText;

import de.mari_023.ae2wtlib.wut.WUTHandler;

public class UpgradeHelper {
    private static boolean readyForUpgrades = false;
    private static final Map<ItemLike, Integer> upgrades = new HashMap<>();

    static void addUpgrades() {
        addUpgradeToAllTerminals(AEItems.ENERGY_CARD, 0);
        addUpgradeToAllTerminals(AE2wtlib.QUANTUM_BRIDGE_CARD, 1);

        Upgrades.add(AE2wtlib.MAGNET_CARD, AEItems.WIRELESS_CRAFTING_TERMINAL, 1);
        Upgrades.add(AE2wtlib.MAGNET_CARD, AE2wtlib.UNIVERSAL_TERMINAL, 1);

        readyForUpgrades = true;
        for (var upgrade : upgrades.entrySet()) {
            addUpgradeToAllTerminals(upgrade.getKey(), upgrade.getValue());
        }
    }

    /**
     * @param upgradeCard  upgrade
     * @param maxSupported how many upgrades of this type a terminal can have, 0 for maximum
     */
    public static void addUpgradeToAllTerminals(ItemLike upgradeCard, int maxSupported) {
        if (readyForUpgrades) {
            if (maxSupported == 0) {
                addMaxUpgradesToAllTerminals(upgradeCard);
                return;
            }
            for (var terminal : WUTHandler.wirelessTerminals.entrySet()) {
                if (!(terminal.getValue().item() instanceof ItemLike item))
                    continue;
                Upgrades.add(upgradeCard, item, maxSupported, GuiText.WirelessTerminals.getTranslationKey());
            }
            Upgrades.add(upgradeCard, AE2wtlib.UNIVERSAL_TERMINAL, maxSupported);
        } else
            upgrades.put(upgradeCard, maxSupported);
    }

    private static void addMaxUpgradesToAllTerminals(ItemLike upgradeCard) {
        Upgrades.add(upgradeCard, AE2wtlib.UNIVERSAL_TERMINAL, WUTHandler.getUpgradeCardCount());
        for (var terminal : WUTHandler.wirelessTerminals.entrySet()) {
            var terminalItem = terminal.getValue().item();
            if (!(terminalItem instanceof ItemLike itemLike))
                break;
            Upgrades.add(upgradeCard, itemLike, 2, GuiText.WirelessTerminals.getTranslationKey());
        }
    }
}
