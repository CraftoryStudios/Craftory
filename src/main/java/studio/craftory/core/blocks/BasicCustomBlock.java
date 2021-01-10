package studio.craftory.core.blocks;


import lombok.NonNull;
import org.bukkit.Location;
import studio.craftory.core.data.CraftoryDirection;

public abstract class BasicCustomBlock extends BaseCustomBlock {

  protected BasicCustomBlock(@NonNull Location location,
      @NonNull CraftoryDirection facingDirection) {
    super(location, facingDirection);
  }

  protected BasicCustomBlock(@NonNull Location location) {
    super(location);
  }
}
