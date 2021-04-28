package studio.craftory.core.resourcepack;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.inject.Inject;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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
  private Map<String, String> itemsToGenerate = new HashMap<>();
  private Map<Material, Set<String>> itemsOfType = new HashMap<>();
  private ObjectMapper mapper = new ObjectMapper();
  @Inject
  private BlockRenderManager blockRenderManager;

  @Override
  public void run() {
    //Need better system later
    if (!Files.exists(Paths.get(Craftory.getInstance().getDataFolder() + "/resourcepacks"))) {
      ResourcePackBuilder.run();
      linkCustomBlockAssets();
      buildItemRenderData();
    }
    ResourcePackBuilt builtEvent = new ResourcePackBuilt();
    Bukkit.getPluginManager().callEvent(builtEvent);
  }

  public void registerBlockAssets(@NonNull CraftoryBlockKey blockKey, Class<? extends CraftoryRenderer> renderer, @NonNull String[] textures) {
    assetsToGenerate.computeIfAbsent(renderer, k -> new HashMap<>()).put(blockKey.toString(), textures);
  }

  public void registerItemForAssignment(@NonNull String itemName, @NonNull String modelPath, @NonNull Material baseMaterial) {
    itemsToGenerate.put(itemName, modelPath);
    Set<String> items = itemsOfType.getOrDefault(baseMaterial, new HashSet<>());
    items.add(itemName);
    itemsOfType.put(baseMaterial, items);
  }

  private void buildItemRenderData() {
    Map<String, Integer> data = null;
    if (Files.exists(Paths.get(ResourcePack.ITEM_RENDER_DATA))) {
      data = readRenderData();
      if (data != null) {
        int id = ResourcePack.ITEM_ID_START_VALUE;
        Set<Integer> usedIds = new HashSet<>(data.values());
        for (String name: itemsToGenerate.keySet()) {
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
      for (String name: itemsToGenerate.keySet()) {
        data.put(name,id);
        id -= 1;
      }
    }

    saveRenderData(data);
    buildItemFiles(data);
  }

  private void buildItemFiles(Map<String, Integer> data) {
    Map<Material, ObjectNode> files = new HashMap<>();
    for (Material material: itemsOfType.keySet()) {
      ObjectNode root = mapper.createObjectNode();
      root.put("parent","minecraft:item/generated");
      ObjectNode textures = mapper.createObjectNode();
      if (material.isBlock()) {
        textures.put("layer0","minecraft:block/" + material.toString().toLowerCase(Locale.ROOT));
      } else {
        textures.put("layer0","minecraft:item/" + material.toString().toLowerCase(Locale.ROOT));
      }
      root.set("textures", textures);
      ArrayNode overrides = mapper.createArrayNode();
      Set<String> items = itemsOfType.get(material);
      for (String item: items) {
        ObjectNode override = mapper.createObjectNode();
        ObjectNode predicate = mapper.createObjectNode();
        predicate.put("custom_model_data", data.get(item));
        override.set("predicate", predicate);
        override.put("model", itemsToGenerate.get(item));
        overrides.add(override);
      }
      root.set("overrides", overrides);
      files.put(material, root);
    }
    saveItemFiles(files);
  }

  private void saveItemFiles(Map<Material, ObjectNode> files) {
    String path = "/assets/minecraft/models/item/";
    String low = ResourcePack.RESOURCE_PACK_PATH + "/low" + path;
    String normal = ResourcePack.RESOURCE_PACK_PATH + "/normal" + path;
    String high = ResourcePack.RESOURCE_PACK_PATH + "/high" + path;
    File file = new File(low);
    file.mkdirs();
    file = new File(normal);
    file.mkdirs();
    file = new File(high);
    file.mkdirs();
    for (Entry<Material, ObjectNode> entry: files.entrySet()) {
      saveObject(low + entry.getKey().toString().toLowerCase(Locale.ROOT), entry.getValue());
      saveObject(normal + entry.getKey().toString().toLowerCase(Locale.ROOT), entry.getValue());
      saveObject(high + entry.getKey().toString().toLowerCase(Locale.ROOT), entry.getValue());
    }
  }

  private void saveObject(String path, ObjectNode objectNode) {
    File file = new File(path);
    try {
      mapper.writeValue(file, objectNode);
    } catch (Exception e) {
      Log.error(e.getMessage());
    }
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

