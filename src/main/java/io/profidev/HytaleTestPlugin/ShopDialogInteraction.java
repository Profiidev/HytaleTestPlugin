package io.profidev.HytaleTestPlugin;

import javax.annotation.Nonnull;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.lookup.CodecMapCodec;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.plugin.PluginBase;
import com.hypixel.hytale.server.core.universe.PlayerRef;

public class ShopDialogInteraction extends SimpleInstantInteraction {
  @Nonnull
  public static final CodecMapCodec<ShopDialogSupplier> PAGE_CODEC = new CodecMapCodec<>();
  @Nonnull
  private final ShopDialogSupplier dialogSupplier;

  public ShopDialogInteraction(@Nonnull PluginBase plugin) {
    super("OpenShopMenu_OpenUI");
    this.dialogSupplier = new ShopDialogSupplier();

    plugin.getCodecRegistry(PAGE_CODEC).register(id, ShopDialogSupplier.class,
        BuilderCodec.builder(ShopDialogSupplier.class, () -> {
          return dialogSupplier;
        }).build());
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
    assert playerRef != null;

    var page = this.dialogSupplier.tryCreate(ref, commandBuffer, playerRef, context);
    if (page == null) {
      return;
    }

    var store = commandBuffer.getStore();
    pageManager.openCustomPage(ref, store, page);
  }
}
