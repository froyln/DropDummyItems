package dev.froyln.dropitems.tweaks;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

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
    public void onClientTick(Minecraft mc)
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
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null || mc.player.containerMenu == null)
        {
            return;
        }

        this.dropDummyItems(mc);
    }

    public String addHeldItem()
    {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null)
        {
            return null;
        }

        ItemStack held = mc.player.getMainHandItem();

        if (held.isEmpty())
        {
            return null;
        }

        Identifier id = BuiltInRegistries.ITEM.getKey(held.getItem());

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
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null)
        {
            return null;
        }

        ItemStack held = mc.player.getMainHandItem();

        if (held.isEmpty())
        {
            return null;
        }

        Identifier id = BuiltInRegistries.ITEM.getKey(held.getItem());

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

    private void dropDummyItems(Minecraft mc)
    {
        if (mc.player == null || mc.gameMode == null || mc.player.containerMenu == null)
        {
            return;
        }

        List<Identifier> dummyIds = this.getDummyIds();

        if (dummyIds.isEmpty())
        {
            return;
        }

        Inventory inventory = mc.player.getInventory();
        boolean behind = FeatureToggle.TWEAK_DROP_ITEMS_BEHIND.isEnabled();

        if (behind)
        {
            rotateYawServerSide(mc, 180.0F);
        }

        for (Slot slot : mc.player.containerMenu.slots)
        {
            // Only the main + hotbar slots (index 0-35 in the player inventory); never armor/offhand
            if (slot.container == inventory && slot.index < 36)
            {
                ItemStack stack = slot.getItem();

                if (stack.isEmpty() == false && this.isDummy(stack, dummyIds))
                {
                    mc.gameMode.handleContainerInput(0, slot.index, 1, ContainerInput.THROW, mc.player);
                }
            }
        }

        if (behind)
        {
            rotateYawServerSide(mc, -180.0F);
        }
    }

    public static void rotateYawServerSide(Minecraft mc, float delta)
    {
        ClientPacketListener connection = mc.getConnection();

        if (mc.player == null || connection == null)
        {
            return;
        }

        connection.send(new ServerboundMovePlayerPacket.Rot(
                mc.player.getYRot() + delta, mc.player.getXRot(), mc.player.onGround(),
                mc.player.horizontalCollision));
    }

    private boolean isInventoryFull(Minecraft mc)
    {
        if (mc.player == null)
        {
            return false;
        }

        for (ItemStack stack : mc.player.getInventory().getNonEquipmentItems())
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
        Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());

        return id != null && dummyIds.contains(id);
    }
}
