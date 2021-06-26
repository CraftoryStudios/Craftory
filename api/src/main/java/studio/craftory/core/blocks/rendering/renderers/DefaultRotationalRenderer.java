package studio.craftory.core.blocks.rendering.renderers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import studio.craftory.core.containers.CraftoryDirection;
import studio.craftory.core.resourcepack.BlockAssetGenerator;
import studio.craftory.core.utils.Log;

public class DefaultRotationalRenderer extends DefaultRenderer {

  @Override
  public void generateAssets(String blockKey, String[] assetsData, BlockAssetGenerator blockAssetGenerator) {
    ObjectMapper mapper = new ObjectMapper();

    if (assetsData.length == 1) {
      ArrayNode renderFileData = mapper.createArrayNode();
      renderFileData.add(this.getClass().getSimpleName());
      for (int i = 0; i < 6; i++) {
        String data = blockAssetGenerator.generateBlockState();
        renderFileData.add(data);
        blockAssetGenerator.addBlockStateToPack(data, assetsData[0], CraftoryDirection.valueOfLabel(i));
      }
      blockAssetGenerator.addToRenderFile(blockKey, renderFileData);
    } else {
      Log.warn("Bad data for asset gen");
    }
  }

}
