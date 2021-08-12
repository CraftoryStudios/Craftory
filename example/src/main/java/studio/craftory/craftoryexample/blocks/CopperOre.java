package studio.craftory.craftoryexample.blocks;

import lombok.NonNull;
import org.bukkit.Location;
import studio.craftory.core.blocks.templates.BaseCustomBlock;
import studio.craftory.core.blocks.templates.ComplexCustomBlock;
import studio.craftory.core.blocks.templates.SimpleCustomBlock;
import studio.craftory.core.containers.CraftoryDirection;


public class CopperOre extends SimpleCustomBlock {

  public CopperOre(@NonNull Location location, @NonNull CraftoryDirection facingDirection) {
    super(location, facingDirection);
  }
}
