package studio.craftory.core.blocks.templates;


import lombok.NonNull;
import org.bukkit.Location;
import studio.craftory.core.containers.CraftoryDirection;

public abstract class SimpleCustomBlock extends BaseCustomBlock {

  protected SimpleCustomBlock(@NonNull Location location,
      @NonNull CraftoryDirection facingDirection) {
    super(location, facingDirection);
  }

}
