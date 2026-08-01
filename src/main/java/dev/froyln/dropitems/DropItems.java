package dev.froyln.dropitems;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ClientModInitializer;

import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.event.TickHandler;

import dev.froyln.dropitems.config.ConfigData;
import dev.froyln.dropitems.event.InputHandler;
import dev.froyln.dropitems.tweaks.DropHandler;

public class DropItems implements ClientModInitializer
{
    public static final Logger logger = LoggerFactory.getLogger(Reference.MOD_ID);

    @Override
    public void onInitializeClient()
    {
        ConfigManager.getInstance().registerConfigHandler(Reference.MOD_ID, new ConfigData());
        InputEventHandler.getKeybindManager().registerKeybindProvider(new InputHandler());
        TickHandler.getInstance().registerClientTickHandler(DropHandler.getInstance());
    }
}
