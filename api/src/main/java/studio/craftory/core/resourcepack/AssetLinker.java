package studio.craftory.core.resourcepack;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.inject.Inject;
import lombok.NonNull;
import org.bukkit.scheduler.BukkitRunnable;
import studio.craftory.core.Craftory;
import studio.craftory.core.blocks.BlockRenderManager;
import studio.craftory.core.blocks.rendering.CraftoryRenderer;
import studio.craftory.core.data.keys.CraftoryBlockKey;

public class AssetLinker extends BukkitRunnable {

  private Map<String, Map<String, String[]>> assetsToGenerate = new HashMap<>();
  private BlockAssetGenerator blockAssetGenerator = new BlockAssetGenerator();

  @Inject
  private BlockRenderManager blockRenderManager;

  @Override
  public void run() {
    CraftorySetup setup = new CraftorySetup();
    setup.runTask(Craftory.getInstance());
    linkCustomBlockAssets();
  }

  public void registerBlockAssets(@NonNull CraftoryBlockKey blockKey, String renderer, @NonNull String[] textures) {
    assetsToGenerate.computeIfAbsent(renderer, k -> new HashMap<>()).put(blockKey.toString(), textures);
  }



  private void linkCustomBlockAssets() {
    for (Entry<String, Map<String, String[]>> rendererAssets : assetsToGenerate.entrySet()) {
      CraftoryRenderer renderer = blockRenderManager.getRenderers().get(rendererAssets.getKey());

      for (Entry<String, String[]> blockAssets : rendererAssets.getValue().entrySet()) {
        renderer.generateAssets(blockAssets.getKey(), blockAssets.getValue(), blockAssetGenerator);
      }
    }
    blockAssetGenerator.writeRenderDataFile();
  }
}

