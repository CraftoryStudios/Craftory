package studio.craftory.core.blocks;

import lombok.NonNull;
import org.bukkit.Location;
import studio.craftory.core.annotations.CustomBlock;
import studio.craftory.core.annotations.RenderData;
import studio.craftory.core.data.CraftoryDirection;
import studio.craftory.core.data.Renderers;

@CustomBlock(renders = {Renderers.BlockStateRender, Renderers.EntitySpawnerRender, Renderers.HeadRender})
@RenderData(
    northFacingModel = "assets/blocks/northexample",
    southFacingModel = "assets/blocks/southexample",
    eastFacingModel = "assets/blocks/eastexample",
    westFacingModel = "assets/blocks/westexample",
    upFacingModel = "assets/blocks/upexample",
    downFacingModel = "assets/blocks/downexample",
    headModel = "assets/blocks/headexample"
)
public class ExampleBlockBase extends BaseCustomBlock {

  protected ExampleBlockBase(@NonNull Location location, @NonNull CraftoryDirection facingDirection) {
    super(location, facingDirection);
  }

}
