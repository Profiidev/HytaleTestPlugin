package io.profidev.HytaleTestPlugin;

import javax.annotation.Nonnull;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.entity.entities.Player;
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

    uiEventBuilder.addEventBinding(CustomUIEventBindingType.SelectedTabChanged, "#MainTabs",
        new EventData().append("@Data", "#MainTabs.SelectedTab").append("Action", "SwitchTab"));
    uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#CloseButton",
        new EventData().append("Action", "ClosePage"));
  }

  @Override
  public void handleDataEvent(@Nonnull Ref<EntityStore> ref,
      @Nonnull Store<EntityStore> store, @Nonnull ShopDialogData data) {
    if (data.action == null) {
      return;
    }

    switch (data.action) {
      case "SwitchTab":
        UICommandBuilder builder = new UICommandBuilder();
        builder.set("#DebugLabel.Text", "Switched to tab: " + data.data);
        sendUpdate(builder);
        break;
      case "ClosePage":
        var player = store.getComponent(ref, Player.getComponentType());
        player.getPageManager().setPage(ref, store, Page.None);
        break;
    }
  }

  public static class ShopDialogData {
    public String action;
    public String data;

    @Nonnull
    public static final BuilderCodec<ShopDialogData> CODEC;

    static {
      var builder = BuilderCodec.builder(ShopDialogData.class,
          ShopDialogData::new);

      var actionCodec = new KeyedCodec<>("Action", Codec.STRING);
      builder.append(actionCodec, (d, v) -> {
        d.action = v;
      }, (d) -> {
        return d.action;
      }).add();

      var dataCodec = new KeyedCodec<>("@Data", Codec.STRING);
      builder.append(dataCodec, (d, v) -> {
        d.data = v;
      }, (d) -> {
        return d.data;
      }).add();

      CODEC = builder.build();
    }
  }
}
