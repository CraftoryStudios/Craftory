package studio.craftory.core.resourcepack;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.inject.Inject;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import studio.craftory.core.Craftory;
import studio.craftory.core.blocks.BlockRenderManager;
import studio.craftory.core.blocks.rendering.CraftoryRenderer;
import studio.craftory.core.data.events.ResourcePackBuilt;
import studio.craftory.core.data.keys.CraftoryBlockKey;

public class AssetLinker extends BukkitRunnable {

  private Map<Class<? extends CraftoryRenderer>, Map<String, String[]>> assetsToGenerate = new HashMap<>();
  private BlockAssetGenerator blockAssetGenerator = new BlockAssetGenerator();

  @Inject
  private BlockRenderManager blockRenderManager;

  @Override
  public void run() {
    //Need better system later
    if (!Files.exists(Paths.get(Craftory.getInstance().getDataFolder() + "/resourcepacks"))) {
      ResourcePackBuilder.run();
      linkCustomBlockAssets();
    }
    ResourcePackBuilt builtEvent = new ResourcePackBuilt();
    Bukkit.getPluginManager().callEvent(builtEvent);
  }

  public void registerBlockAssets(@NonNull CraftoryBlockKey blockKey, Class<? extends CraftoryRenderer> renderer, @NonNull String[] textures) {
    assetsToGenerate.computeIfAbsent(renderer, k -> new HashMap<>()).put(blockKey.toString(), textures);
  }



  private void linkCustomBlockAssets() {
    for (Entry<Class<? extends CraftoryRenderer>, Map<String, String[]>> rendererAssets : assetsToGenerate.entrySet()) {
      CraftoryRenderer renderer = blockRenderManager.getRenderers().get(rendererAssets.getKey().getSimpleName());

      for (Entry<String, String[]> blockAssets : rendererAssets.getValue().entrySet()) {
        renderer.generateAssets(blockAssets.getKey(), blockAssets.getValue(), blockAssetGenerator);
      }
    }
    blockAssetGenerator.writeRenderDataFile();
  }
}

