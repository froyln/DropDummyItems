package dev.froyln.dropitems.tweaks;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;

import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.interfaces.IClientTickHandler;
import fi.dy.masa.malilib.util.InfoUtils;

import dev.froyln.dropitems.Reference;
import dev.froyln.dropitems.config.Configs;
import dev.froyln.dropitems.config.FeatureToggle;

public class DropHandler implements IClientTickHandler
{
    private static final DropHandler INSTANCE = new DropHandler();

    private boolean wasInventoryFull = false;

    public static DropHandler getInstance()
    {
        return INSTANCE;
    }

    @Override
    public void onClientTick(MinecraftClient mc)
    {
        if (mc.player == null)
        {
            this.wasInventoryFull = false;
            return;
        }

        boolean full = this.isInventoryFull(mc);

        if (full && this.wasInventoryFull == false && Configs.ALERT_INVENTORY_FULL.getBooleanValue())
        {
            InfoUtils.printActionbarMessage("dropdummyitems.alert.inventory_full");
        }

        this.wasInventoryFull = full;

        if (full && FeatureToggle.TWEAK_AUTO_DROP_DUMMY_ON_FULL.isEnabled())
        {
            this.dropDummyItems(mc);
        }
    }

    public void dropAllDummyItems()
    {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.player == null || mc.player.currentScreenHandler == null)
        {
            return;
        }

        this.dropDummyItems(mc);
    }

    public String addHeldItem()
    {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.player == null)
        {
            return null;
        }

        ItemStack held = mc.player.getMainHandStack();

        if (held.isEmpty())
        {
            return null;
        }

        Identifier id = Registries.ITEM.getId(held.getItem());

        if (id == null)
        {
            return null;
        }

        List<String> strings = new ArrayList<>(Configs.DUMMY_ITEMS.getStrings());
        String entry = id.toString();

        if (strings.contains(entry))
        {
            return null;
        }

        strings.add(entry);
        Configs.DUMMY_ITEMS.setStrings(strings);
        ConfigManager.getInstance().onConfigsChanged(Reference.MOD_ID);

        return entry;
    }

    public String removeHeldItem()
    {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.player == null)
        {
            return null;
        }

        ItemStack held = mc.player.getMainHandStack();

        if (held.isEmpty())
        {
            return null;
        }

        Identifier id = Registries.ITEM.getId(held.getItem());

        if (id == null)
        {
            return null;
        }

        List<String> strings = new ArrayList<>(Configs.DUMMY_ITEMS.getStrings());
        String removed = strings.stream()
                .filter(s -> id.equals(Identifier.tryParse(s)))
                .findFirst().orElse(null);

        if (removed != null)
        {
            strings.removeIf(s -> id.equals(Identifier.tryParse(s)));
            Configs.DUMMY_ITEMS.setStrings(strings);
            ConfigManager.getInstance().onConfigsChanged(Reference.MOD_ID);
        }

        return removed;
    }

    private void dropDummyItems(MinecraftClient mc)
    {
        if (mc.player == null || mc.interactionManager == null || mc.player.currentScreenHandler == null)
        {
            return;
        }

        List<Identifier> dummyIds = this.getDummyIds();

        if (dummyIds.isEmpty())
        {
            return;
        }

        PlayerInventory inventory = mc.player.getInventory();

        for (Slot slot : mc.player.currentScreenHandler.slots)
        {
            // Only the main + hotbar slots (index 0-35 in the player inventory); never armor/offhand
            if (slot.inventory == inventory && slot.getIndex() < 36)
            {
                ItemStack stack = slot.getStack();

                if (stack.isEmpty() == false && this.isDummy(stack, dummyIds))
                {
                    mc.interactionManager.clickSlot(0, slot.id, 1, SlotActionType.THROW, mc.player);
                }
            }
        }
    }

    private boolean isInventoryFull(MinecraftClient mc)
    {
        if (mc.player == null)
        {
            return false;
        }

        for (ItemStack stack : mc.player.getInventory().main)
        {
            if (stack.isEmpty())
            {
                return false;
            }
        }

        return true;
    }

    private List<Identifier> getDummyIds()
    {
        List<Identifier> ids = new ArrayList<>();

        for (String entry : Configs.DUMMY_ITEMS.getStrings())
        {
            Identifier id = Identifier.tryParse(entry.trim());

            if (id != null)
            {
                ids.add(id);
            }
        }

        return ids;
    }

    private boolean isDummy(ItemStack stack, List<Identifier> dummyIds)
    {
        Identifier id = Registries.ITEM.getId(stack.getItem());

        return id != null && dummyIds.contains(id);
    }
}
