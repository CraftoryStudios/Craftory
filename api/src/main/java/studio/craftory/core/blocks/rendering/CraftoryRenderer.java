package studio.craftory.core.blocks.rendering;

import org.bukkit.block.Block;
import studio.craftory.core.containers.RenderData;
import studio.craftory.core.containers.CraftoryDirection;
import studio.craftory.core.resourcepack.BlockAssetGenerator;

public interface CraftoryRenderer {
  void render(Block block, CraftoryDirection direction, RenderData renderData);

  void generateAssets(String blockKey, String[] assetsData, BlockAssetGenerator blockAssetGenerator);
}
