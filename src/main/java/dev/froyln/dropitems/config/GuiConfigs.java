package dev.froyln.dropitems.config;

import java.util.Collections;
import java.util.List;

import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.util.StringUtils;

import dev.froyln.dropitems.Reference;

public class GuiConfigs extends GuiConfigsBase
{
    private static ConfigGuiTab tab = ConfigGuiTab.TWEAKS;

    public GuiConfigs()
    {
        super(10, 50, Reference.MOD_ID, null, "dropdummyitems.gui.title");
    }

    @Override
    public void initGui()
    {
        super.initGui();

        this.clearOptions();

        int x = 10;
        int y = 26;

        for (ConfigGuiTab tab : ConfigGuiTab.values())
        {
            x += this.createTabButton(x, y, tab) + 2;
        }
    }

    private int createTabButton(int x, int y, ConfigGuiTab tab)
    {
        ButtonGeneric button = new ButtonGeneric(x, y, -1, 20, tab.getDisplayName());
        button.setEnabled(GuiConfigs.tab != tab);
        this.addButton(button, new ButtonListener(tab, this));

        return button.getWidth();
    }

    @Override
    public List<ConfigOptionWrapper> getConfigs()
    {
        switch (GuiConfigs.tab)
        {
            case LISTS:
                return ConfigOptionWrapper.createFor(Collections.singletonList(Configs.DUMMY_ITEMS));

            case HOTKEYS:
                return ConfigOptionWrapper.createFor(Configs.getAllHotkeys());

            case ALERTS:
                return ConfigOptionWrapper.createFor(Collections.singletonList(Configs.ALERT_INVENTORY_FULL));

            default:
                return ConfigOptionWrapper.createFor(FeatureToggle.getToggles());
        }
    }

    private static class ButtonListener implements IButtonActionListener
    {
        private final GuiConfigs parent;
        private final ConfigGuiTab tab;

        public ButtonListener(ConfigGuiTab tab, GuiConfigs parent)
        {
            this.tab = tab;
            this.parent = parent;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton)
        {
            GuiConfigs.tab = this.tab;

            this.parent.reCreateListWidget();
            this.parent.getListWidget().resetScrollbarPosition();
            this.parent.initGui();
        }
    }

    public enum ConfigGuiTab
    {
        TWEAKS ("dropdummyitems.gui.tab.tweaks"),
        LISTS  ("dropdummyitems.gui.tab.lists"),
        HOTKEYS("dropdummyitems.gui.tab.hotkeys"),
        ALERTS ("dropdummyitems.gui.tab.alerts");

        private final String translationKey;

        private ConfigGuiTab(String translationKey)
        {
            this.translationKey = translationKey;
        }

        public String getDisplayName()
        {
            return StringUtils.translate(this.translationKey);
        }
    }
}
