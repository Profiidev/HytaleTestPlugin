package io.profidev.HytaleTestPlugin;

import javax.annotation.Nonnull;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class UICommand extends AbstractPlayerCommand {
  public UICommand() {
    super("ui", "Opens the custom UI page.");
  }

  @Override
  protected void execute(@Nonnull CommandContext ctx, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref,
      @Nonnull PlayerRef playerRef, @Nonnull World world) {
    var player = store.getComponent(ref, Player.getComponentType());

    if (player == null) {
      ctx.sendMessage(Message.raw("This command can only be executed by a player."));
      return;
    }

    var pageManager = player.getPageManager();
    if (pageManager.getCustomPage() != null) {
      return;
    }

    pageManager.openCustomPage(ref, store, new ShopDialogPage(playerRef));
  }
}
