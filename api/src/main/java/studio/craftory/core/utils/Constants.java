package studio.craftory.core.utils;

import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import studio.craftory.core.Craftory;
import studio.craftory.core.containers.keys.ItemDataKey;

@UtilityClass
public class Constants {

  public static class Keys {
    private Keys(){}

    public static final NamespacedKey BLOCK_ITEM_KEY = new NamespacedKey(Craftory.getInstance(), "blockItemKey");
    public static final ItemDataKey BLOCK_ITEM_DATA_KEY = new ItemDataKey(BLOCK_ITEM_KEY, PersistentDataType.STRING);
    public static final NamespacedKey ITEM_NAME_NAMESPACED_KEY = new NamespacedKey(Craftory.getInstance(), "CUSTOM_ITEM_NAME");

    public static final NamespacedKey CHARGE_NAMESPACED_KEY = new NamespacedKey(Craftory.getInstance(), "CHARGE");
    public static final NamespacedKey MAX_CHARGE_NAMESPACED_KEY = new NamespacedKey(Craftory.getInstance(), "MAX_CHARGE");

  }

  public static class ResourcePack {
    private ResourcePack(){}

    public static final String RESOURCE_PACK_PATH = Craftory.getInstance().getDataFolder() +"/resourcepacks";
    public static final String ASSETS_PATH = Craftory.getInstance().getDataFolder() + "/assets";
    public static final String ITEMS_PATH = "/assets/minecraft/models/item/";
    public static final String JSON = ".json";
    public static final String TEMP_PATH = Craftory.getInstance().getDataFolder() + "/temp";
    public static final String ITEM_RENDER_DATA = Craftory.getInstance().getDataFolder() + "/ItemRenderData.json";
    public static final int ITEM_ID_START_VALUE = -1000;
    @Getter
    private static final String[] QUALITIES = {"low", "normal", "high"};
  }

  public static class BlockLists {
    public static final List<Material> REPLACEABLE_BLOCKS = Arrays
        .asList(Material.SNOW, Material.VINE, Material.GRASS, Material.TALL_GRASS, Material.SEAGRASS, Material.FERN,
            Material.LARGE_FERN);
  }

}
