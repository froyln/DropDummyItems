package dev.froyln.dropitems.config;

import java.util.Arrays;
import java.util.List;

import fi.dy.masa.malilib.config.options.ConfigBoolean;

public enum FeatureToggle
{
    TWEAK_AUTO_DROP_DUMMY_ON_FULL("autoDropDummyOnFull", false,
            "Automatically drop all dummy items whenever the inventory becomes full."),
    TWEAK_DROP_ITEMS_BEHIND("dropItemsBehind", false,
            "Drop dummy items behind you (180 degrees from your current view) instead of in front.");

    private final ConfigBoolean config;

    private FeatureToggle(String name, boolean defaultValue, String comment)
    {
        this.config = new ConfigBoolean(name, defaultValue, comment);
    }

    public boolean isEnabled()
    {
        return this.config.getBooleanValue();
    }

    public void toggle()
    {
        this.config.setBooleanValue(!this.config.getBooleanValue());
    }

    public ConfigBoolean getConfig()
    {
        return this.config;
    }

    public static List<ConfigBoolean> getToggles()
    {
        return Arrays.stream(values()).map(FeatureToggle::getConfig).toList();
    }
}
