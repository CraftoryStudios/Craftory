package studio.craftory.core.resourcepack;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.inject.Inject;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import studio.craftory.core.Craftory;
import studio.craftory.core.blocks.BlockRenderManager;
import studio.craftory.core.blocks.rendering.CraftoryRenderer;
import studio.craftory.core.data.events.ResourcePackBuilt;
import studio.craftory.core.data.keys.CraftoryBlockKey;
import studio.craftory.core.utils.Constants.ResourcePack;
import studio.craftory.core.utils.Log;

public class AssetLinker extends BukkitRunnable {

  private Map<Class<? extends CraftoryRenderer>, Map<String, String[]>> assetsToGenerate = new HashMap<>();
  private BlockAssetGenerator blockAssetGenerator = new BlockAssetGenerator();
  private List<String> itemsToGenerate = new ArrayList<>();
  private ObjectMapper mapper = new ObjectMapper();
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

  public void registerItemForAssignment(@NonNull String itemName) {
    itemsToGenerate.add(itemName);
  }

  private void buildItemRenderData() {
    Map<String, Integer> data = null;
    if (Files.exists(Paths.get(ResourcePack.ITEM_RENDER_DATA))) {
      data = readRenderData();
      if (data != null) {
        int id = ResourcePack.ITEM_ID_START_VALUE;
        Set<Integer> usedIds = new HashSet<>(data.values());
        for (String name: itemsToGenerate) {
          if (!data.containsKey(name)) {
            while (usedIds.contains(id)) {
              id -= 1;
            }
            data.put(name, id);
            id -= 1;
          }

        }
      }
    }
    if (data == null){
      data = new HashMap<>();
      int id = ResourcePack.ITEM_ID_START_VALUE;
      for (String name: itemsToGenerate) {
        data.put(name,id);
        id -= 1;
      }
    }
    saveRenderData(data);
  }

  private Map<String, Integer> readRenderData() {
    File file = new File(ResourcePack.ITEM_RENDER_DATA);
    try {
      return  mapper.readValue(file, new TypeReference<Map<String, Integer>>(){});
    } catch (Exception e) {
      Log.warn("Failed to read existing item render data");
      Log.warn(e.toString());
    }
    return null;
  }

  private void saveRenderData(Map<String, Integer> data) {
    File file = new File(ResourcePack.ITEM_RENDER_DATA);
    try {
      mapper.writeValue(file, data);
    } catch (Exception e) {
      Log.warn("Failed to save item render data");
      Log.warn(e.toString());
    }
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

