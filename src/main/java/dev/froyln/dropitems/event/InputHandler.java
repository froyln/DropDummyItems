package dev.froyln.dropitems.event;

import java.util.List;

import net.minecraft.client.Minecraft;

import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.hotkeys.IKeybindManager;
import fi.dy.masa.malilib.hotkeys.IKeybindProvider;
import fi.dy.masa.malilib.util.InfoUtils;

import dev.froyln.dropitems.Reference;
import dev.froyln.dropitems.config.Configs;
import dev.froyln.dropitems.config.FeatureToggle;
import dev.froyln.dropitems.config.GuiConfigs;
import dev.froyln.dropitems.tweaks.DropHandler;

public class InputHandler implements IKeybindProvider
{
    public InputHandler()
    {
        Configs.OPEN_CONFIGS.getKeybind().setCallback((action, key) -> {
            Minecraft.getInstance().setScreenAndShow(new GuiConfigs());
            return true;
        });

        Configs.DROP_ALL.getKeybind().setCallback((action, key) -> {
            DropHandler.getInstance().dropAllDummyItems();
            InfoUtils.printActionbarMessage("dropdummyitems.hotkey.drop_all");
            return true;
        });

        Configs.ADD_HELD.getKeybind().setCallback((action, key) -> {
            String entry = DropHandler.getInstance().addHeldItem();

            if (entry != null)
            {
                InfoUtils.printActionbarMessage("dropdummyitems.hotkey.add_held", entry);
            }

            return true;
        });

        Configs.REMOVE_HELD.getKeybind().setCallback((action, key) -> {
            String entry = DropHandler.getInstance().removeHeldItem();

            if (entry != null)
            {
                InfoUtils.printActionbarMessage("dropdummyitems.hotkey.remove_held", entry);
            }

            return true;
        });

        Configs.TOGGLE_AUTO_DROP.getKeybind().setCallback((action, key) -> {
            FeatureToggle.TWEAK_AUTO_DROP_DUMMY_ON_FULL.toggle();
            InfoUtils.printActionbarMessage("dropdummyitems.hotkey.toggle_auto_drop",
                    FeatureToggle.TWEAK_AUTO_DROP_DUMMY_ON_FULL.isEnabled());
            return true;
        });
    }

    @Override
    public void addHotkeys(IKeybindManager manager)
    {
        manager.addHotkeysForCategory(Reference.MOD_NAME, "dropdummyitems.hotkeys.category", getHotkeys());
    }

    @Override
    public void addKeysToMap(IKeybindManager manager)
    {
        for (IHotkey hotkey : getHotkeys())
        {
            manager.addKeybindToMap(hotkey.getKeybind());
        }
    }

    private static List<? extends IHotkey> getHotkeys()
    {
        return List.of(Configs.OPEN_CONFIGS, Configs.DROP_ALL, Configs.ADD_HELD, Configs.REMOVE_HELD, Configs.TOGGLE_AUTO_DROP);
    }
}
