package io.profidev.HytaleTestPlugin;

import com.hypixel.hytale.assetstore.AssetExtraInfo.Data;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.OpenCustomUIInteraction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.WorldConfig;
import com.hypixel.hytale.server.core.universe.world.worldgen.provider.VoidWorldGenProvider;

import java.lang.reflect.Field;
import java.util.Collections;

import javax.annotation.Nonnull;

public class HytaleTestPlugin extends JavaPlugin {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public HytaleTestPlugin(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        LOGGER.atInfo().log("Hytale Test Plugin is setting up!");

        this.getCommandRegistry().registerCommand(new WorldCommand());
        this.getCommandRegistry().registerCommand(new SpawnShopCommand());
        OpenCustomUIInteraction.registerCustomPageSupplier(this, ShopDialogSupplier.class, "OpenShopMenu",
                new ShopDialogSupplier());

        var interaction = new OpenCustomUIInteraction();

        Field idFiled;
        try {
            idFiled = OpenCustomUIInteraction.class.getDeclaredField("customPageSupplier");
            idFiled.setAccessible(true);
            idFiled.set(interaction, new ShopDialogSupplier());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            LOGGER.atSevere().withCause(e).log("Failed to set customPageSupplier field via reflection");
        }

        try {
            idFiled = Interaction.class.getDeclaredField("id");
            idFiled.setAccessible(true);
            idFiled.set(interaction, "OpenShopMenu_OpenUI");
            Field dataField = Interaction.class.getDeclaredField("data");
            dataField.setAccessible(true);
            dataField.set(interaction, new Data(Interaction.class, "OpenShopMenu_OpenUI", null));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            LOGGER.atSevere().withCause(e).log("Failed to set id field via reflection");
        }

        Interaction.getAssetStore().loadAssets("OpenShopMenu", Collections.singletonList(interaction));

        var rootInteraction = new RootInteraction("OpenShopMenu", new String[] { "OpenShopMenu_OpenUI" });
        rootInteraction.build();
        RootInteraction.getAssetStore().loadAssets("OpenShopMenu", Collections.singletonList(rootInteraction));
    }

    @Override
    protected void start() {
        var universe = Universe.get();
        var name = "test_world";

        for (var worldName : universe.getWorlds().keySet()) {
            if (worldName.equals("default")) {
                var world = universe.getWorlds().get("default");
                LOGGER.atInfo().log("World 'default' %s", world.getWorldConfig().getWorldGenProvider());
            }
        }

        for (var world : universe.getWorlds().keySet()) {
            LOGGER.atInfo().log("Existing world: %s", world);
            if (world.equals(name)) {
                LOGGER.atInfo().log("World '%s' already exists, skipping creation.", name);
                return;
            }
        }

        LOGGER.atInfo().log("Creating world '%s'", name);
        var config = new WorldConfig();
        config.setWorldGenProvider(new VoidWorldGenProvider());
        var path = universe.getPath().resolve("worlds").resolve(name);
        if (path == null) {
            LOGGER.atSevere().log("Failed to resolve path for world '%s'", name);
            return;
        }

        path.toFile().mkdirs();
        universe.makeWorld(name, path, config);
    }
}
