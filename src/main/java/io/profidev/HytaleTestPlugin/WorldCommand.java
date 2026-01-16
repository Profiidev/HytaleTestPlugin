package io.profidev.HytaleTestPlugin;

import javax.annotation.Nonnull;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.NotificationUtil;

public class WorldCommand extends AbstractPlayerCommand {
  public WorldCommand() {
    super("worldtest", "A test command for world functionality");
  }

  @Override
  protected void execute(@Nonnull CommandContext ctx, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref,
      @Nonnull PlayerRef playerRef, @Nonnull World world) {
    var player = store.getComponent(ref, Player.getComponentType());

    if (player == null) {
      ctx.sendMessage(Message.raw("This command can only be executed by a player."));
      return;
    }

    var test_name = "test_world";
    var name = world.getName().equals(test_name) ? "default" : test_name;
    var universe = Universe.get();
    var targetWorld = universe.getWorlds().get(name);

    var teleport = new Teleport(targetWorld, new Transform(0, 120, 0));
    world.execute(() -> {
      store.addComponent(ref, Teleport.getComponentType(), teleport);
    });

    var message = Message.raw("Teleported to world: " + name).color("#008000ff");
    NotificationUtil.sendNotification(playerRef.getPacketHandler(), message);
  }
}
