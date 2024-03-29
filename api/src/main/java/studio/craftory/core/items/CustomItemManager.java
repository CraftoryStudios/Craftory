package studio.craftory.core.items;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import studio.craftory.core.containers.events.ResourcePackBuilt;
import studio.craftory.core.resourcepack.AssetLinker;
import studio.craftory.core.utils.Constants.ResourcePack;
import studio.craftory.core.utils.Log;

public class CustomItemManager implements Listener {

  protected static final Map<String, CustomItem> customItemCache = new HashMap<>();
  private static final Map<String, CustomItem> unqiueItemCache = new HashMap<>();
  private static final Set<String> duplicateItemNames = new HashSet<>();
  private Map<String, Integer> customItemRenderIdCache = new HashMap<>();

  @Inject
  private AssetLinker assetLinker;

  private static Gson gson = new GsonBuilder().disableHtmlEscaping().create();

  protected CustomItemManager() {

  }

  public static boolean isDuplicateItemName(@NonNull String name) {
    return duplicateItemNames.contains(name);
  }

  public static boolean isUniqueItemName(@NonNull String name) {
    return unqiueItemCache.containsKey(name);
  }

  public static Optional<ItemStack> getUniqueItem(@NonNull String name) {
    if (unqiueItemCache.containsKey(name)) {
      return Optional.of(unqiueItemCache.get(name).getItem());
    }
    return Optional.empty();
  }

  /* Registering */
  public void registerCustomItem(@NonNull CustomItem item) {
    String itemName = item.getUniqueName();
    assetLinker.registerItemForAssignment(itemName, item.getModelPath(), item.getMaterial());
    customItemCache.put(itemName, item);
    Log.debug("Registered custom item '" + itemName + "'");
    if (item.hasHoldEffects()) {
      ItemEventManager.registerItemOnHoldEffects(itemName, item.getHoldEffects());
    }
    if (item.hasHandlers()) {
      item.getHandlers().forEach(ItemEventManager::registerDumbEvent);
    }

    String commonName = item.getName();
    if (!unqiueItemCache.containsKey(commonName) && !duplicateItemNames.contains(commonName)) {
      unqiueItemCache.put(commonName, item);
    } else {
      unqiueItemCache.remove(commonName);
      duplicateItemNames.add(commonName);
    }
  }

  @EventHandler
  public void onResourcePackBuilt(ResourcePackBuilt e) {
    assignRenderIds();
  }

  private void assignRenderIds() {
    File renderIdFile = new File(ResourcePack.ITEM_RENDER_DATA);
    if(renderIdFile.exists()) {
      try {
        customItemRenderIdCache = gson.fromJson(new FileReader(renderIdFile), new TypeToken<Map<String, Integer>>(){}.getType());
        Log.debug("Loaded item render data: " + customItemRenderIdCache.toString());
      } catch (IOException e) {
        Log.error("Couldn't read item render data");
      }
    } else {
      Log.warn("No item render data found");
    }

    for (CustomItem item : customItemCache.values()) {
      String itemName = item.getUniqueName();
      if (!customItemRenderIdCache.containsKey(itemName)) {
        throw new IllegalArgumentException(
            "Custom item not present in the render data, all items must have an ID for render texture! ItemName: " + itemName);
      }
      item.createItem(customItemRenderIdCache.get(itemName));
    }
  }

  public Optional<ItemStack> getCustomItem(@NonNull String name) {
    if (customItemCache.containsKey(name)) {
      return Optional.of(customItemCache.get(name).getItem());
    }
    return Optional.empty();
  }

  public Optional<ItemStack> getCustomItemOrDefault(@NonNull String name) {
    if (customItemCache.containsKey(name)) {
      return Optional.of(customItemCache.get(name).getItem());
    }
    Optional<Material> material = Optional.ofNullable(Material.getMaterial(name));
    return material.map(ItemStack::new);
  }

  public boolean isCustomItemName(@NonNull String name) {
    return customItemCache.containsKey(name);
  }

}
