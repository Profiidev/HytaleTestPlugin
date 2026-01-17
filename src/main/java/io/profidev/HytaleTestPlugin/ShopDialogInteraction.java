package io.profidev.HytaleTestPlugin;

import javax.annotation.Nonnull;

import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;

public class ShopDialogInteraction extends SimpleInstantInteraction {
  public ShopDialogInteraction() {
    super("OpenShopMenu_OpenUI");
  }

  @Override
  protected void firstRun(@Nonnull InteractionType type, @Nonnull InteractionContext context,
      @Nonnull CooldownHandler cooldownHandler) {
    var ref = context.getEntity();
    var commandBuffer = context.getCommandBuffer();
    if (commandBuffer == null) {
      return;
    }

    var playerComponent = commandBuffer.getComponent(ref, Player.getComponentType());
    if (playerComponent == null) {
      return;
    }

    var pageManager = playerComponent.getPageManager();
    if (pageManager.getCustomPage() != null) {
      return;
    }

    var playerRef = commandBuffer.getComponent(ref, PlayerRef.getComponentType());
    if (playerRef == null) {
      return;
    }

    var store = commandBuffer.getStore();
    pageManager.openCustomPage(ref, store, new ShopDialogPage(playerRef));
  }
}
