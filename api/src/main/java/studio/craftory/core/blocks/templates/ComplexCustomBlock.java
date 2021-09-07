package studio.craftory.core.blocks.templates;

import lombok.NonNull;
import org.bukkit.Location;
import studio.craftory.core.containers.CraftoryDirection;
import studio.craftory.core.containers.persitanceholders.DataHolder;
import studio.craftory.core.containers.persitanceholders.PersistentDataHolder;
import studio.craftory.core.containers.persitanceholders.VolatileDataHolder;

public abstract class ComplexCustomBlock extends BaseCustomBlock implements PersistentDataHolder, VolatileDataHolder {

  private final DataHolder persistentData = new DataHolder();
  private final DataHolder volatileData = new DataHolder();

  protected ComplexCustomBlock(@NonNull Location location,
      @NonNull CraftoryDirection facingDirection) {
    super(location, facingDirection);
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
