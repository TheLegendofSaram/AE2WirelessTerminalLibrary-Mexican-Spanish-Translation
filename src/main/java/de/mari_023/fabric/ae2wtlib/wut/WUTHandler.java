package de.mari_023.fabric.ae2wtlib.wut;

import appeng.container.ContainerLocator;
import de.mari_023.fabric.ae2wtlib.Config;
import de.mari_023.fabric.ae2wtlib.terminal.ItemWT;
import de.mari_023.fabric.ae2wtlib.wct.ItemWCT;
import de.mari_023.fabric.ae2wtlib.wit.ItemWIT;
import de.mari_023.fabric.ae2wtlib.wpt.ItemWPT;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WUTHandler {

    public static String getCurrentTerminal(ItemStack wirelessUniversalTerminal) {
        if(!(wirelessUniversalTerminal.getItem() instanceof ItemWT) || wirelessUniversalTerminal.getTag() == null)
            return "noTerminal";
        if(!(wirelessUniversalTerminal.getItem() instanceof ItemWUT)) {
            if(wirelessUniversalTerminal.getItem() instanceof ItemWCT) return "crafting";
            else if(wirelessUniversalTerminal.getItem() instanceof ItemWPT) return "pattern";
            else if(wirelessUniversalTerminal.getItem() instanceof ItemWIT) return "interface";
            else return "noTerminal";
        }
        String currentTerminal = wirelessUniversalTerminal.getTag().getString("currentTerminal");

        if(!wirelessTerminals.containsKey(currentTerminal)) for(String terminal : terminalNames)
            if(wirelessUniversalTerminal.getTag().getBoolean(terminal)) {
                currentTerminal = terminal;
                wirelessUniversalTerminal.getTag().putString("currentTerminal", currentTerminal);
                break;
            }
        return currentTerminal;
    }

    public static void setCurrentTerminal(ItemStack itemStack, String terminal) {//FIXME sync to client
        if(hasTerminal(itemStack, terminal)) {
            assert itemStack.getTag() != null;
            itemStack.getTag().putString("currentTerminal", terminal);
        }
    }

    public static boolean hasTerminal(ItemStack itemStack, String terminal) {
        if(!terminalNames.contains(terminal)) return false;
        if(itemStack.getTag() == null) return false;
        return itemStack.getTag().getBoolean(terminal);
    }

    public static void cycle(ItemStack itemStack) {//FIXME sync to client
        if(itemStack.getTag() == null) return;
        String nextTerminal = getCurrentTerminal(itemStack);
        do {
            int i = terminalNames.indexOf(nextTerminal) + 1;
            if(i == terminalNames.size()) i = 0;
            nextTerminal = terminalNames.get(i);
        } while(!itemStack.getTag().getBoolean(nextTerminal));
        itemStack.getTag().putString("currentTerminal", nextTerminal);
    }

    public static void open(final PlayerEntity player, final ContainerLocator locator) {
        int slot = locator.getItemIndex();
        ItemStack is;
        if(slot >= 100 && slot < 200 && Config.allowTrinket())
            is = TrinketsApi.getTrinketsInventory(player).getStack(slot - 100);
        else is = player.inventory.getStack(slot);

        if(is.getTag() == null) return;
        String currentTerminal = getCurrentTerminal(is);
        if(!wirelessTerminals.containsKey(currentTerminal)) {
            player.sendMessage(new LiteralText("This terminal does not contain any other Terminals"), false);
            return;
        }
        wirelessTerminals.get(currentTerminal).open(player, locator);
    }

    private static final HashMap<String, containerOpener> wirelessTerminals = new HashMap<>();
    private static final List<String> terminalNames = new ArrayList<>();

    public static void addTerminal(String Name, containerOpener open) {
        if(terminalNames.contains(Name)) return;
        wirelessTerminals.put(Name, open);
        terminalNames.add(Name);
    }

    @FunctionalInterface
    public interface containerOpener {
        void open(final PlayerEntity player, final ContainerLocator locator);
    }
}