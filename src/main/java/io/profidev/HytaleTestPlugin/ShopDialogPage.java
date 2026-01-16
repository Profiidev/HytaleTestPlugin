package io.profidev.HytaleTestPlugin;

import javax.annotation.Nonnull;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec.Builder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class ShopDialogPage extends InteractiveCustomUIPage<ShopDialogPage.ShopDialogData> {

  public ShopDialogPage(@Nonnull PlayerRef playerRef) {
    super(playerRef, CustomPageLifetime.CanDismiss, ShopDialogData.CODEC);
  }

  @Override
  public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder uiCommandBuilder,
      @Nonnull UIEventBuilder uiEventBuilder,
      @Nonnull Store<EntityStore> store) {
    uiCommandBuilder.append("Pages/ShopDialog.ui");
    uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#GreetButton",
        new EventData().append("@PlayerName", "#NameInput.Value"));
  }

  @Override
  public void handleDataEvent(@Nonnull Ref<EntityStore> ref,
      @Nonnull Store<EntityStore> store, @Nonnull ShopDialogData data) {
    var playerName = data.Action != null ? data.Action : "unknown";
    playerRef.sendMessage(Message.raw("Hello, " + playerName + "! Welcome to the shop."));
  }

  public static class ShopDialogData {
    @Nonnull
    private static final Builder<ShopDialogData> BUILDER = BuilderCodec.builder(ShopDialogData.class,
        ShopDialogData::new);
    @Nonnull
    public static final BuilderCodec<ShopDialogData> CODEC = BUILDER
        .append(new KeyedCodec("@PlayerName", Codec.STRING), (d, v) -> {
          d.Action = v;
        }, (d) -> {
          return d.Action;
        }).add().build();
    public String Action;
  }
}
