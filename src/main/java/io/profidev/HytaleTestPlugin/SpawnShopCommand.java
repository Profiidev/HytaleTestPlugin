package io.profidev.HytaleTestPlugin;

import java.util.HashMap;

import javax.annotation.Nonnull;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
import com.hypixel.hytale.server.core.modules.entity.component.Interactable;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.PersistentModel;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.modules.interaction.Interactions;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.NotificationUtil;

public class SpawnShopCommand extends AbstractPlayerCommand {
  private Ref<EntityStore> shop;

  public SpawnShopCommand() {
    super("spawnshop", "Spawns a shop npc");
  }

  @Override
  protected void execute(@Nonnull CommandContext ctx, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref,
      @Nonnull PlayerRef playerRef, @Nonnull World world) {
    var transform = playerRef.getTransform();

    var holder = EntityStore.REGISTRY.newHolder();

    var modelAsset = ModelAsset.getAssetMap().getAsset("Klops_Merchant");
    if (modelAsset == null) {
      var message = Message.raw("Model asset not found!");
      NotificationUtil.sendNotification(playerRef.getPacketHandler(), message);
      return;
    }
    var model = Model.createScaledModel(modelAsset, 1.0f);

    var interactionsMap = new HashMap<InteractionType, String>();
    interactionsMap.put(InteractionType.Use, "OpenShopMenu");
    var interactions = new Interactions(interactionsMap);

    holder.addComponent(TransformComponent.getComponentType(),
        new TransformComponent(transform.getPosition(), transform.getRotation()));
    holder.addComponent(PersistentModel.getComponentType(), new PersistentModel(model.toReference()));
    holder.addComponent(ModelComponent.getComponentType(), new ModelComponent(model));
    holder.addComponent(BoundingBox.getComponentType(), new BoundingBox(model.getBoundingBox()));
    holder.addComponent(NetworkId.getComponentType(), new NetworkId(store.getExternalData().takeNextNetworkId()));
    holder.addComponent(Interactions.getComponentType(), interactions);

    holder.ensureComponent(UUIDComponent.getComponentType());
    holder.ensureComponent(Interactable.getComponentType());

    world.execute(() -> {
      if (shop != null) {
        store.removeEntity(shop, RemoveReason.REMOVE);
      }
      shop = store.addEntity(holder, AddReason.SPAWN);
    });
  }
}
