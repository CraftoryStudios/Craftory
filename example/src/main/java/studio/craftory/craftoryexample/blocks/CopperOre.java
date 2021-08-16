package studio.craftory.craftoryexample.blocks;

import lombok.NonNull;
import org.bukkit.Location;
import studio.craftory.core.blocks.CustomBlock;
import studio.craftory.core.containers.CraftoryDirection;


public class CopperOre extends CustomBlock {

  public CopperOre(@NonNull Location location, @NonNull CraftoryDirection facingDirection) {
    super(location, facingDirection);
  }
}
