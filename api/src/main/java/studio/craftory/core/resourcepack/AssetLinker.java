package studio.craftory.core.resourcepack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.EnumMap;
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
import studio.craftory.core.blocks.BlockRenderer;
import studio.craftory.core.blocks.rendering.CraftoryRenderer;
import studio.craftory.core.containers.events.ResourcePackBuilt;
import studio.craftory.core.containers.keys.CraftoryBlockKey;
import studio.craftory.core.utils.Constants.ResourcePack;
import studio.craftory.core.utils.Log;

public class AssetLinker extends BukkitRunnable {

  private Map<Class<? extends CraftoryRenderer>, Map<String, String[]>> assetsToGenerate = new HashMap<>();
  private BlockAssetGenerator blockAssetGenerator = new BlockAssetGenerator();
  private Map<String, String> itemsToGenerate = new HashMap<>();
  private Map<Material, Set<String>> itemsOfType = new EnumMap<>(Material.class);
  private Gson gson = new GsonBuilder().disableHtmlEscaping().create();
  @Inject
  private BlockRenderer blockRenderer;

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
    Craftory.getInstance().onResourcesSetup();
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

  private void linkCustomBlockAssets() {
    for (Entry<Class<? extends CraftoryRenderer>, Map<String, String[]>> rendererAssets : assetsToGenerate.entrySet()) {
      CraftoryRenderer renderer = blockRenderer.getRenderers().get(rendererAssets.getKey().getSimpleName());

      for (Entry<String, String[]> blockAssets : rendererAssets.getValue().entrySet()) {
        renderer.generateAssets(blockAssets.getKey(), blockAssets.getValue(), blockAssetGenerator);
      }
    }
    blockAssetGenerator.writeRenderDataFile();
  }

  private void buildItemRenderData() {
    Log.debug("Building render data");
    Map<String, Integer> data = null;
    if (Files.exists(Paths.get(ResourcePack.ITEM_RENDER_DATA))) {
      data = readRenderData();
      if (data != null) {
        buildFromExistingItemRenderData(data);
      }
    }
    if (data == null){
      data = buildFreshItemRenderData();
    }
    Log.debug("Render data: " + data.toString());
    saveRenderData(data);
    buildItemFiles(data);
  }

  private Map<String, Integer> readRenderData() {
    File file = new File(ResourcePack.ITEM_RENDER_DATA);
    try {
      return  gson.fromJson(new FileReader(file), new TypeToken<Map<String, Integer>>(){}.getType());
    } catch (Exception e) {
      Log.warn("Failed to read existing item render data");
      Log.warn(e.toString());
    }
    return null;
  }

  private Map<String, Integer> buildFromExistingItemRenderData(Map<String, Integer> data) {
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
    return data;
  }

  private Map<String, Integer> buildFreshItemRenderData() {
    Map<String, Integer> data = new HashMap<>();
    int id = ResourcePack.ITEM_ID_START_VALUE;
    for (String name: itemsToGenerate.keySet()) {
      data.put(name,id);
      id -= 1;
    }
    return data;
  }

  private void saveRenderData(Map<String, Integer> data) {
    File file = new File(ResourcePack.ITEM_RENDER_DATA);
    try (FileWriter fw = new FileWriter(file)){
      gson.toJson(data, fw);
    } catch (Exception e) {
      Log.warn("Failed to save item render data");
      Log.warn(e.toString());
    }
  }

  private void buildItemFiles(Map<String, Integer> data) {
    Map<Material, JsonObject> files = new EnumMap<>(Material.class);
    for (Entry<Material, Set<String>> entry: itemsOfType.entrySet()) {
      // Build JSON for the item model file
      Material material = entry.getKey();
      JsonObject root = new JsonObject();
      root.addProperty("parent","minecraft:item/generated");

      root.add("textures", createItemTextureJSON(material));

      root.add("overrides", createItemOverridesJSON(entry.getValue(), data));

      files.put(material, root);
    }
    Log.debug("Item files " + files.toString());
    for (String quality: ResourcePack.getQUALITIES()) {
      saveItemFiles(files, quality);
    }
  }

  private JsonObject createItemTextureJSON(Material material) {
    JsonObject textures = new JsonObject();
    if (material.isBlock()) {
      textures.addProperty("layer0","minecraft:block/" + material.toString().toLowerCase(Locale.ROOT));
    } else {
      textures.addProperty("layer0","minecraft:item/" + material.toString().toLowerCase(Locale.ROOT));
    }
    return textures;
  }

  private JsonArray createItemOverridesJSON(Set<String> items, Map<String, Integer> data) {
    JsonArray overrides = new JsonArray();
    for (String item: items) {
      JsonObject override = new JsonObject();
      JsonObject predicate = new JsonObject();
      predicate.addProperty("custom_model_data", data.get(item));
      override.add("predicate", predicate);
      override.addProperty("model", itemsToGenerate.get(item));
      overrides.add(override);
    }
    return overrides;
  }

  private void saveItemFiles(Map<Material, JsonObject> files, String quality) {
    String path = Paths.get(ResourcePack.RESOURCE_PACK_PATH, File.separator, quality, ResourcePack.ITEMS_PATH).toString() + File.separator;
    File file = new File(path);
    file.mkdirs();
    for (Entry<Material, JsonObject> entry: files.entrySet()) {
      saveObject(path + entry.getKey().toString().toLowerCase(Locale.ROOT) + ResourcePack.JSON, entry.getValue());
    }
  }

  private void saveObject(String path, JsonObject objectNode) {
    File file = new File(path);
    try (FileWriter fw = new FileWriter(file)){
      gson.toJson(objectNode, fw);
    } catch (Exception e) {
      Log.error(e.getMessage());
    }
  }

}

