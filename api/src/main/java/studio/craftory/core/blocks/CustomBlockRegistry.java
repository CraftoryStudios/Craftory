package studio.craftory.core.blocks;

import static studio.craftory.core.utils.Constants.Keys.blockItemDataKey;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Synchronized;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;
import studio.craftory.core.blocks.templates.BaseCustomBlock;
import studio.craftory.core.data.CraftoryDirection;
import studio.craftory.core.data.keys.CraftoryBlockKey;
import studio.craftory.core.data.keys.CraftoryDataKey;
import studio.craftory.core.executors.AsyncExecutionManager;
import studio.craftory.core.executors.SyncExecutionManager;
import studio.craftory.core.items.CustomItem;
import studio.craftory.core.utils.Log;

/** Class based on LogisticsCraft's Logistics-API (MIT) and the LogisticsTypeRegister class **/
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class CustomBlockRegistry {

  @Inject
  private AsyncExecutionManager asyncExecutionManager;
  @Inject
  private SyncExecutionManager syncExecutionManager;

  private final Map<CraftoryBlockKey, Constructor<? extends BaseCustomBlock>> blockTypes = new HashMap<>();
  private final Map<Class<? extends BaseCustomBlock>, CraftoryBlockKey> blockKeys = new HashMap<>();

  private final Map<String, CraftoryDataKey> craftoryDataKeyMap = new HashMap<>();
  private final Map<String, CraftoryBlockKey> craftoryBlockKeyMap = new HashMap<>();

  @Synchronized
  public CraftoryBlockKey registerCustomBlockClass(@NonNull Plugin plugin, @NonNull Class<?> block) {
    CraftoryBlockKey craftoryBlockKey = new CraftoryBlockKey(plugin, block);
    if (!blockTypes.containsKey(craftoryBlockKey)) {

        Optional<Constructor<?>> constructor = getConstructor(block);
        if (constructor.isPresent()) {

          addCustomBlockKeys((Class<? extends BaseCustomBlock>) block, (Constructor<? extends BaseCustomBlock>) constructor.get(), craftoryBlockKey);
          registerCustomBlockTickables((Class<? extends BaseCustomBlock>) block);

          CustomItem.builder().name(craftoryBlockKey.getName()).displayName(
              craftoryBlockKey.getName()).attribute(blockItemDataKey, craftoryBlockKey.toString()).material(
              Material.STONE).build().register(plugin);
          Log.debug("CustomBlock Register: " + block.getName());
        } else {
          Log.warn("Couldn't get constructor for custom block: " + craftoryBlockKey.getName());
        }

    } else {
      Log.warn("Trying to re-register known key of Custom Block: " + craftoryBlockKey.getName());
    }
    return craftoryBlockKey;
  }

  @Synchronized
  public boolean isBlockClassRegistered(@NonNull BaseCustomBlock block) {
    return blockKeys.containsKey(block.getClass());
  }

  public Optional<BaseCustomBlock> getNewCustomBlockInstance(@NonNull CraftoryBlockKey key,  @NonNull Location location,
      @NonNull CraftoryDirection direction) {
    Optional<Constructor<? extends BaseCustomBlock>> constructor = Optional.ofNullable(blockTypes.get(key));
    Location blockLocation = location.getBlock().getLocation();

    if (constructor.isPresent()) {
      try {
        return Optional.of(constructor.get().newInstance(blockLocation, direction));
      } catch (Exception e) {
        Log.error("Couldn't create custom block of type: "+key.getName() + " at location: "+location);
        return Optional.empty();
      }
    }
    return Optional.empty();
  }

  @Synchronized
  public Optional<CraftoryBlockKey> getBlockKey(@NonNull BaseCustomBlock block) {
    return getBlockKey(block.getClass());
  }

  @Synchronized
  public Optional<CraftoryBlockKey> getBlockKey(@NonNull Class<? extends BaseCustomBlock> block) {
    return Optional.ofNullable(blockKeys.get(block));
  }

  @Synchronized
  public Optional<CraftoryBlockKey> getBlockKey(@NonNull String key) {return Optional.ofNullable(craftoryBlockKeyMap.get(key));}

  private Optional<Constructor<?>> getConstructor(@NonNull Class<?> clazz) {
    try {
      return Optional.of(clazz.getDeclaredConstructor(Location.class, CraftoryDirection.class));
    } catch(NoSuchMethodException e) {
      return Optional.empty();
    }
  }

  public void registerDataKey(String key, CraftoryDataKey dataKey) {
    craftoryDataKeyMap.putIfAbsent(key, dataKey);
  }

  public Optional<CraftoryDataKey> getDataKey(@NonNull String key) {
    return Optional.ofNullable(craftoryDataKeyMap.get(key));
  }

  private void addCustomBlockKeys(Class<? extends BaseCustomBlock> clazz, Constructor<? extends BaseCustomBlock> constructor, CraftoryBlockKey key) {
    blockTypes.put(key, constructor);
    blockKeys.put(clazz, key);
    craftoryBlockKeyMap.put(key.toString(), key);
  }

  private void registerCustomBlockTickables(Class<? extends BaseCustomBlock> block) {
    asyncExecutionManager.registerTickableClass(block);
    syncExecutionManager.registerTickableClass(block);
  }

}
