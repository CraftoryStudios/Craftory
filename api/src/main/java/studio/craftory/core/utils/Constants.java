package studio.craftory.core.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import studio.craftory.core.Craftory;
import studio.craftory.core.data.keys.ItemDataKey;

@UtilityClass
public class Constants {

  public static class Keys {

    public static final NamespacedKey blockItemKey = new NamespacedKey(Craftory.getInstance(), "blockItemKey");
    public static final ItemDataKey blockItemDataKey = new ItemDataKey(blockItemKey, PersistentDataType.STRING);
    public static final NamespacedKey ITEM_NAME_NAMESPACED_KEY = new NamespacedKey(Craftory.getInstance(), "CUSTOM_ITEM_NAME");

    public static final NamespacedKey CHARGE_NAMESPACED_KEY = new NamespacedKey(Craftory.getInstance(), "CHARGE");
    public static final NamespacedKey MAX_CHARGE_NAMESPACED_KEY = new NamespacedKey(Craftory.getInstance(), "MAX_CHARGE");

  }

  public static class ResourcePack {
    public static final String resourcePackPath = Craftory.getInstance().getDataFolder() +"/resourcepacks";
    public static final String assetsPath = Craftory.getInstance().getDataFolder() + "/assets";
    public static final String tempPath = Craftory.getInstance().getDataFolder() + "/temp";
  }

}
