package studio.craftory.core.tests;

import lombok.NonNull;
import org.bukkit.Location;
import studio.craftory.core.blocks.templates.BaseCustomBlock;
import studio.craftory.core.data.CraftoryDirection;

public class TestCustomBlock extends BaseCustomBlock {

  protected TestCustomBlock(@NonNull Location location, @NonNull CraftoryDirection facingDirection) {
    super(location, facingDirection);
  }
}
