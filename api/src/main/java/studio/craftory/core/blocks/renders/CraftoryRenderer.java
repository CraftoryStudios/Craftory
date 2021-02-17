package studio.craftory.core.blocks.renders;

import org.bukkit.block.Block;
import studio.craftory.core.data.CraftoryDirection;

public interface CraftoryRenderer {
  void render(Block block, CraftoryDirection direction, RenderData renderData);
}
