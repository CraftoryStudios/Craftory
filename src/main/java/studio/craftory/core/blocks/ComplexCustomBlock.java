package studio.craftory.core.blocks;

import lombok.NonNull;
import org.bukkit.Location;
import studio.craftory.core.annotations.Persistent;
import studio.craftory.core.data.CraftoryDirection;
import studio.craftory.core.data.persitanceholders.DataHolder;
import studio.craftory.core.data.persitanceholders.PersistentDataHolder;
import studio.craftory.core.data.persitanceholders.VolatileDataHolder;
import studio.craftory.core.executors.interfaces.Tickable;

public abstract class ComplexCustomBlock extends BaseCustomBlock implements PersistentDataHolder, VolatileDataHolder, Tickable {

  @Persistent()
  private DataHolder persistentData = new DataHolder();
  private DataHolder volatileData = new DataHolder();

  protected ComplexCustomBlock(@NonNull Location location,
      @NonNull CraftoryDirection facingDirection) {
    super(location, facingDirection);
  }

  protected ComplexCustomBlock(@NonNull Location location) {
    super(location);
  }

  @Override
  public DataHolder getPersistentData() {
    return persistentData;
  }

  @Override
  public DataHolder getVolatileData() {
    return volatileData;
  }
}
