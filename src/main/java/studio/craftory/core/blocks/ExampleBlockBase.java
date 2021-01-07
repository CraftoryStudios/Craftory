package studio.craftory.core.blocks;

import lombok.NonNull;
import org.bukkit.Location;
import studio.craftory.core.annotations.CustomBlock;
import studio.craftory.core.data.CraftoryDirection;
import studio.craftory.core.data.RenderData;

@CustomBlock
public class ExampleBlockBase extends BaseCustomBlock {

  /** Rendering Data **/
  private static final RenderData renderData;
  static {
    renderData = RenderData.builder()
                .northModel("assets/blocks/ExampleBlockBase")
                .build();
  }

  protected ExampleBlockBase(@NonNull Location location, @NonNull CraftoryDirection facingDirection) {
    super(location, facingDirection);
  }

  @Override
  protected RenderData getRenderData() {
    return renderData;
  }
}
