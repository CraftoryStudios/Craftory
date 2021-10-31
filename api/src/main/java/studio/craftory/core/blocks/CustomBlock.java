package studio.craftory.core.blocks;

import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataContainer;
import studio.craftory.core.Craftory;
import studio.craftory.core.blocks.storage.CustomBlockData;
import studio.craftory.core.blocks.storage.StorageKeys;
import studio.craftory.core.blocks.storage.StorageTypes;
import studio.craftory.core.containers.CraftoryDirection;
import studio.craftory.core.containers.persitanceholders.DataHolder;
import studio.craftory.core.containers.persitanceholders.PersistentDataHolder;
import studio.craftory.core.containers.persitanceholders.VolatileDataHolder;

public abstract class CustomBlock implements PersistentDataHolder, VolatileDataHolder {
  private CustomBlockData persistentData;
  private DataHolder volatileData = new DataHolder();
  private final Location location;
  private CraftoryDirection facingDirection;


  protected CustomBlock(@NonNull Location location,
      @NonNull CraftoryDirection facingDirection) {
    this.location = location;
    this.facingDirection = facingDirection;
    persistentData = new CustomBlockData(location, Craftory.getInstance());
    persistentData.set(StorageKeys.directionKey, StorageTypes.DIRECTION, facingDirection);
    persistentData.set(StorageKeys.blockType, StorageTypes.BLOCK_KEY, Craftory.blockRegistry().getBlockKey(this).get());
  }

  @Override
  public PersistentDataContainer getPersistentData() {
    return persistentData;
  }

  @Override
  public DataHolder getVolatileData() {
    return volatileData;
  }

  public void renderCustomBlock() {}

  public void onPlayerClick(PlayerInteractEvent playerInteractEvent) {}

  public Location getLocation() {return this.location;}

  public CraftoryDirection getFacingDirection() {return this.facingDirection;}
}
