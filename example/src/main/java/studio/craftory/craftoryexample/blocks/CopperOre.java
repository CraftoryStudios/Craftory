package studio.craftory.craftoryexample.blocks;

import lombok.NonNull;
import org.bukkit.Location;
import studio.craftory.core.blocks.templates.ComplexCustomBlock;
import studio.craftory.core.data.CraftoryDirection;


public class CopperOre extends ComplexCustomBlock {

  public CopperOre(@NonNull Location location, @NonNull CraftoryDirection facingDirection) {
    super(location, facingDirection);
  }
}
