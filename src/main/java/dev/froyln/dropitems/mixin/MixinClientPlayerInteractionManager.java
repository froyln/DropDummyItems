package dev.froyln.dropitems.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

import dev.froyln.dropitems.config.FeatureToggle;
import dev.froyln.dropitems.tweaks.DropHandler;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager
{
    @Inject(method = "clickSlot", at = @At("HEAD"))
    private void dropdummyitems$beforeClickSlot(int syncId, int slotId, int button,
            SlotActionType actionType, PlayerEntity player, CallbackInfo ci)
    {
        if (this.dropdummyitems$isCraftingResultThrow(slotId, actionType))
        {
            DropHandler.rotateYawServerSide(MinecraftClient.getInstance(), 180.0F);
        }
    }

    @Inject(method = "clickSlot", at = @At("RETURN"))
    private void dropdummyitems$afterClickSlot(int syncId, int slotId, int button,
            SlotActionType actionType, PlayerEntity player, CallbackInfo ci)
    {
        if (this.dropdummyitems$isCraftingResultThrow(slotId, actionType))
        {
            DropHandler.rotateYawServerSide(MinecraftClient.getInstance(), -180.0F);
        }
    }

    private boolean dropdummyitems$isCraftingResultThrow(int slotId, SlotActionType actionType)
    {
        if (actionType != SlotActionType.THROW || FeatureToggle.TWEAK_DROP_CRAFTED_BEHIND.isEnabled() == false)
        {
            return false;
        }

        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.player == null || slotId < 0)
        {
            return false;
        }

        ScreenHandler screenHandler = mc.player.currentScreenHandler;

        if (screenHandler == null || slotId >= screenHandler.slots.size())
        {
            return false;
        }

        Slot slot = screenHandler.slots.get(slotId);

        return slot instanceof CraftingResultSlot;
    }
}
