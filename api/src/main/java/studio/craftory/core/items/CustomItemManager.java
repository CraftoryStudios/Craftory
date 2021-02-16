package studio.craftory.core.items;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import studio.craftory.core.Craftory;
import studio.craftory.core.utils.Log;

public class CustomItemManager {

  protected static final Map<String, CustomItem> customItemCache = new HashMap<>();
  private Map<String, Integer> customItemRenderIdCache = new HashMap<>();

  public static final NamespacedKey ITEM_NAME_NAMESPACED_KEY = new NamespacedKey(Craftory.getInstance(), "CUSTOM_ITEM_NAME");

  protected CustomItemManager() {
    File renderIdFile = new File(Craftory.getInstance().getDataFolder(), "ItemRenderData.json");
    ObjectMapper mapper = new ObjectMapper();
    if(renderIdFile.exists()) {
      try {
        customItemRenderIdCache = mapper.readValue(renderIdFile, new TypeReference<Map<String, Integer>>() {});
        Log.info("Loaded item render data");
        customItemRenderIdCache.forEach((x,y) -> Log.info(x + " : " + y));
      } catch (IOException e) {
        Log.error("Couldn't read item render data");
      }
    } else {
      Log.warn("No item render data found");
    }
  }

  /* Registering */
  public void registerCustomItem(@NonNull CustomItem item) {
    String itemName = item.getUniqueName();
    if(!customItemRenderIdCache.containsKey(itemName)) {
      throw new IllegalArgumentException("Custom item not present in the render data, all items must have an ID for render texture!");
    }
    customItemCache.put(itemName, item);
    item.createItem(customItemRenderIdCache.get(itemName));
    Log.info("Registered custom item '" + itemName + "'");
    if (item.hasHoldEffects()) {
      ItemEventManager.registerItemOnHoldEffects(itemName, item.getHoldEffects());
    }
    if (item.hasHandlers()) {
      item.getHandlers().forEach(ItemEventManager::registerDumbEvent);
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
