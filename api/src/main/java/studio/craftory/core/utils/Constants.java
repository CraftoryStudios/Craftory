package studio.craftory.core.utils;

import java.io.File;
import lombok.experimental.UtilityClass;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import studio.craftory.core.Craftory;
import studio.craftory.core.data.keys.ItemDataKey;

@UtilityClass
public class Constants {

  @UtilityClass
  public static class Keys {

    public final NamespacedKey blockItemKey = new NamespacedKey(Craftory.getInstance(), "blockItemKey");
    public final ItemDataKey blockItemDataKey = new ItemDataKey(blockItemKey, PersistentDataType.STRING);
    public final NamespacedKey ITEM_NAME_NAMESPACED_KEY = new NamespacedKey(Craftory.getInstance(), "CUSTOM_ITEM_NAME");

    public final NamespacedKey CHARGE_NAMESPACED_KEY = new NamespacedKey(Craftory.getInstance(), "CHARGE");
    public final NamespacedKey MAX_CHARGE_NAMESPACED_KEY = new NamespacedKey(Craftory.getInstance(), "MAX_CHARGE");

  }

  @UtilityClass
  public static class ResourcePack {
    public final String resourcePackPath = Craftory.getInstance().getDataFolder() + File.pathSeparator + "resourcepacks";
    public final String assetsPath = Craftory.getInstance().getDataFolder() + File.pathSeparator + "assets";
    public final String tempPath = Craftory.getInstance().getDataFolder() + File.pathSeparator + "temp";
  }

}
