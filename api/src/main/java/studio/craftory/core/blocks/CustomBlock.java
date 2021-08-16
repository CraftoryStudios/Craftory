package studio.craftory.core.blocks;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataContainer;
import studio.craftory.core.Craftory;
import studio.craftory.core.blocks.storage.CraftoryBlockKeyDataType;
import studio.craftory.core.blocks.storage.CraftoryDirectionDataType;
import studio.craftory.core.blocks.storage.CustomBlockData;
import studio.craftory.core.containers.CraftoryDirection;
import studio.craftory.core.containers.keys.CraftoryBlockKey;
import studio.craftory.core.containers.persitanceholders.DataHolder;
import studio.craftory.core.containers.persitanceholders.PersistentDataHolder;
import studio.craftory.core.containers.persitanceholders.VolatileDataHolder;

public abstract class CustomBlock implements PersistentDataHolder, VolatileDataHolder {

  private static NamespacedKey facingKey = new NamespacedKey(Craftory.getInstance(), "facing");
  private static NamespacedKey blockType = new NamespacedKey(Craftory.getInstance(), "type");

  private CustomBlockData persistentData;
  private DataHolder volatileData = new DataHolder();
  @Getter
  private final Location location;

  @Getter
  private CraftoryDirection facingDirection;


  protected CustomBlock(@NonNull Location location,
      @NonNull CraftoryDirection facingDirection) {
    this.location = location;
    this.facingDirection = facingDirection;
    // TODO use plugin registering block
    persistentData = new CustomBlockData(location, Craftory.getInstance());
    // TODO store static instance of this
    persistentData.set(facingKey, new CraftoryDirectionDataType(), facingDirection);
    persistentData.set(blockType, new CraftoryBlockKeyDataType(), Craftory.blockRegistry().getBlockKey(this).get());
  }

  @Override
  public PersistentDataContainer getPersistentData() {
    return persistentData;
  }

  @Override
  public DataHolder getVolatileData() {
    return volatileData;
  }

  public void renderCustomBlock() {

  }

  public void onPlayerClick(PlayerInteractEvent playerInteractEvent) {

  }
}
