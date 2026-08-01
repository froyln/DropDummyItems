package dev.froyln.dropitems.config;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.config.options.ConfigStringList;

public class Configs
{
    public static final ConfigStringList DUMMY_ITEMS = new ConfigStringList(
            "dummyItems",
            ImmutableList.of("minecraft:dirt", "minecraft:cobblestone"),
            "The item identifiers that are considered junk. Bare ids like 'diamond' work too.");

    public static final ConfigHotkey OPEN_CONFIGS = new ConfigHotkey(
            "openConfigs", "X,C", "Open the config GUI");

    public static final ConfigHotkey DROP_ALL = new ConfigHotkey(
            "dropAllDummyItems", "", "Drop all dummy items from your inventory");

    public static final ConfigHotkey ADD_HELD = new ConfigHotkey(
            "addHeldItem", "", "Add the item in your main hand to the dummy list");

    public static final ConfigHotkey REMOVE_HELD = new ConfigHotkey(
            "removeHeldItem", "", "Remove the item in your main hand from the dummy list");

    public static final ConfigHotkey TOGGLE_AUTO_DROP = new ConfigHotkey(
            "toggleAutoDropDummyOnFull", "", "Toggle auto-drop when the inventory is full");

    public static List<IConfigBase> getAllHotkeys()
    {
        return ImmutableList.of(OPEN_CONFIGS, DROP_ALL, ADD_HELD, REMOVE_HELD, TOGGLE_AUTO_DROP);
    }

    public static List<IConfigBase> getAllConfigs()
    {
        List<IConfigBase> configs = new ArrayList<>(getAllHotkeys());
        configs.add(DUMMY_ITEMS);

        for (FeatureToggle toggle : FeatureToggle.values())
        {
            configs.add(toggle.getConfig());
        }

        return configs;
    }
}
