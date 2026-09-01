package dev.froyln.dropitems.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;

import dev.froyln.dropitems.config.FeatureToggle;
import dev.froyln.dropitems.tweaks.DropHandler;

@Mixin(MultiPlayerGameMode.class)
public class MixinClientPlayerInteractionManager
{
    @Inject(method = "handleContainerInput", at = @At("HEAD"))
    private void dropdummyitems$beforeHandleContainerInput(int containerId, int slotIndex, int button,
            ContainerInput actionType, Player player, CallbackInfo ci)
    {
        if (this.dropdummyitems$isCraftingResultThrow(slotIndex, actionType))
        {
            DropHandler.rotateYawServerSide(Minecraft.getInstance(), 180.0F);
        }
    }

    @Inject(method = "handleContainerInput", at = @At("RETURN"))
    private void dropdummyitems$afterHandleContainerInput(int containerId, int slotIndex, int button,
            ContainerInput actionType, Player player, CallbackInfo ci)
    {
        if (this.dropdummyitems$isCraftingResultThrow(slotIndex, actionType))
        {
            DropHandler.rotateYawServerSide(Minecraft.getInstance(), -180.0F);
        }
    }

    private boolean dropdummyitems$isCraftingResultThrow(int slotIndex, ContainerInput actionType)
    {
        if (actionType != ContainerInput.THROW || FeatureToggle.TWEAK_DROP_CRAFTED_BEHIND.isEnabled() == false)
        {
            return false;
        }

        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null || slotIndex < 0)
        {
            return false;
        }

        AbstractContainerMenu menu = mc.player.containerMenu;

        if (menu == null || slotIndex >= menu.slots.size())
        {
            return false;
        }

        Slot slot = menu.slots.get(slotIndex);

        return slot instanceof ResultSlot;
    }
}
