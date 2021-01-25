package studio.craftory.craftoryexample.blocks;

import lombok.NonNull;
import org.bukkit.Location;
import studio.craftory.core.annotations.CustomBlock;
import studio.craftory.core.annotations.RenderData;
import studio.craftory.core.blocks.templates.ComplexCustomBlock;
import studio.craftory.core.data.CraftoryDirection;
import studio.craftory.core.blocks.renders.Renderers;

@CustomBlock(renders = {Renderers.BLOCK_STATE_RENDER, Renderers.ENTITY_SPAWNER_RENDER, Renderers.HEAD_RENDER})
@RenderData(
    northFacingModel = "assets/blocks/northexample",
    southFacingModel = "assets/blocks/southexample",
    eastFacingModel = "assets/blocks/eastexample",
    westFacingModel = "assets/blocks/westexample",
    upFacingModel = "assets/blocks/upexample",
    downFacingModel = "assets/blocks/downexample",
    headModel = "assets/blocks/headexample"
)
public class ExampleBlockBase extends ComplexCustomBlock {

  protected ExampleBlockBase(@NonNull Location location, @NonNull CraftoryDirection facingDirection) {
    super(location, facingDirection);
  }

}
