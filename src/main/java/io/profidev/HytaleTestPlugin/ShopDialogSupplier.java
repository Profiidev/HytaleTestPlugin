package io.profidev.HytaleTestPlugin;

import javax.annotation.Nullable;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.player.pages.CustomUIPage;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.OpenCustomUIInteraction.CustomPageSupplier;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class ShopDialogSupplier implements CustomPageSupplier {
  private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

  @Override
  @Nullable
  public CustomUIPage tryCreate(Ref<EntityStore> ref, ComponentAccessor<EntityStore> componentAccessor,
      PlayerRef playerRef,
      InteractionContext interactionContext) {
    LOGGER.atInfo().log("Supplying ShopDialogPage.");
    if (playerRef == null) {
      return null;
    }
    return new ShopDialogPage(playerRef);
  }
}
