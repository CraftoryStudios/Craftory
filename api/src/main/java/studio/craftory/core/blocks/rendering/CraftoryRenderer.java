package studio.craftory.core.blocks.rendering;

import org.bukkit.block.Block;
import studio.craftory.core.data.RenderData;
import studio.craftory.core.data.CraftoryDirection;

public interface CraftoryRenderer {
  void render(Block block, CraftoryDirection direction, RenderData renderData);

  void generateAssets(String blockKey, String[] assetsData);
}
