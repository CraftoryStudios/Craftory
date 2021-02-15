package studio.craftory.core.items;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import studio.craftory.core.Craftory;
import studio.craftory.core.utils.Log;

@NoArgsConstructor
public class CustomItemManager {

  protected static final Map<String, CustomItem> customItemCache = new HashMap<>();

  public static final NamespacedKey ITEM_NAME_NAMESPACED_KEY = new NamespacedKey(Craftory.getInstance(), "CUSTOM_ITEM_NAME");

  /* Registering */
  public void registerCustomItem(CustomItem item) {
    String itemName = item.getUniqueName();
    customItemCache.put(itemName, item);
    Log.info("Registered custom item '" + itemName + "'");
    if (item.hasHoldEffects()) {
      ItemEventManager.registerItemOnHoldEffects(itemName, item.getHoldEffects());
    }
    if (item.hasHandlers()) {
      item.getHandlers().forEach(ItemEventManager::registerDumbEvent);
    }
  }

  public ItemStack getCustomItem(String name) {
    if (customItemCache.containsKey(name)) {
      return customItemCache.get(name).getItem();
    }
    return new ItemStack(Material.AIR);
  }

  public ItemStack getCustomItemOrDefault(String name) {
    if (customItemCache.containsKey(name)) {
      return customItemCache.get(name).getItem();
    }
    Optional<Material> material = Optional.ofNullable(Material.getMaterial(name));
    return material.map(ItemStack::new).orElseGet(() -> new ItemStack(Material.AIR));
  }

  public boolean isCustomItemName(String name) {
    return customItemCache.containsKey(name);
  }

}
