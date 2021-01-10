package studio.craftory.core.blocks;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Location;
import studio.craftory.core.annotations.Persistent;
import studio.craftory.core.data.CraftoryDirection;
import studio.craftory.core.data.persitanceholders.DataHolder;
import studio.craftory.core.data.persitanceholders.PersistentDataHolder;
import studio.craftory.core.data.persitanceholders.VolatileDataHolder;
import studio.craftory.core.data.safecontainers.SafeBlockLocation;
import studio.craftory.core.executors.interfaces.Tickable;

public abstract class BaseCustomBlock {

  @Getter
  private SafeBlockLocation safeBlockLocation;

  @Persistent
  @Getter
  private CraftoryDirection facingDirection;


  protected BaseCustomBlock(@NonNull Location location, @NonNull CraftoryDirection facingDirection) {
    this.safeBlockLocation = new SafeBlockLocation(location);
    this.facingDirection = facingDirection;
  }

  public void changeFacingDirection(CraftoryDirection facingDirection) {
    this.facingDirection = facingDirection;
  }

}
