package studio.craftory.core.tests;

import lombok.NonNull;
import org.bukkit.Location;
import studio.craftory.core.blocks.CustomBlock;
import studio.craftory.core.containers.CraftoryDirection;

public class TestCustomBlock extends CustomBlock {

  protected TestCustomBlock(@NonNull Location location, @NonNull CraftoryDirection facingDirection) {
    super(location, facingDirection);
  }
}
