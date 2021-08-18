package studio.craftory.core.blocks.rendering.renderers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import studio.craftory.core.containers.CraftoryDirection;
import studio.craftory.core.resourcepack.BlockAssetGenerator;
import studio.craftory.core.utils.Log;

public class DefaultRotationalRenderer extends DefaultRenderer {
  private static Gson gson = new Gson();

  @Override
  public void generateAssets(String blockKey, String[] assetsData, BlockAssetGenerator blockAssetGenerator) {
    if (assetsData.length == 1) {
      JsonArray renderFileData = new JsonArray();
      renderFileData.add(this.getClass().getSimpleName());
      for (int i = 0; i < 6; i++) {
        String data = blockAssetGenerator.generateBlockState();
        renderFileData.add(data);
        blockAssetGenerator.addBlockStateToPack(data, assetsData[0], CraftoryDirection.valueOfLabel((byte)i));
      }
      blockAssetGenerator.addToRenderFile(blockKey, renderFileData);
    } else {
      Log.warn("Bad data for asset gen");
    }
  }

}
