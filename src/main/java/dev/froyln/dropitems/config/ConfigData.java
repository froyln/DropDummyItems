package dev.froyln.dropitems.config;

import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.fabricmc.loader.api.FabricLoader;

import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigHandler;

import dev.froyln.dropitems.DropItems;
import dev.froyln.dropitems.Reference;

public class ConfigData implements IConfigHandler
{
    private static final String CONFIG_FILE_NAME = Reference.MOD_ID + ".json";

    private final Path configFile;
    private final Gson gson = new Gson();

    public ConfigData()
    {
        this.configFile = FabricLoader.getInstance().getConfigDir().resolve(CONFIG_FILE_NAME);
    }

    @Override
    public void load()
    {
        JsonObject root = this.readConfigFile();

        if (root != null)
        {
            for (IConfigBase config : Configs.getAllConfigs())
            {
                String name = config.getName();

                if (root.has(name))
                {
                    config.setValueFromJsonElement(root.get(name));
                }
            }
        }
    }

    @Override
    public void save()
    {
        JsonObject root = new JsonObject();

        for (IConfigBase config : Configs.getAllConfigs())
        {
            root.add(config.getName(), config.getAsJsonElement());
        }

        try
        {
            Files.createDirectories(this.configFile.getParent());
            Files.writeString(this.configFile, this.gson.toJson(root));
        }
        catch (Exception e)
        {
            DropItems.logger.warn("Failed to save config file '{}'", this.configFile, e);
        }
    }

    private JsonObject readConfigFile()
    {
        if (Files.exists(this.configFile) == false)
        {
            return null;
        }

        try
        {
            JsonElement element = JsonParser.parseString(Files.readString(this.configFile));

            if (element.isJsonObject())
            {
                return element.getAsJsonObject();
            }
        }
        catch (Exception e)
        {
            DropItems.logger.warn("Failed to read config file '{}'", this.configFile, e);
        }

        return null;
    }
}
