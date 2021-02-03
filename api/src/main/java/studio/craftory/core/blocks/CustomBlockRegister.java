package studio.craftory.core.blocks;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import javax.inject.Inject;
import jdk.nashorn.internal.ir.IfNode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Synchronized;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import studio.craftory.core.blocks.templates.BaseCustomBlock;
import studio.craftory.core.data.CraftoryDirection;
import studio.craftory.core.data.keys.CraftoryDataKey;
import studio.craftory.core.data.keys.CustomBlockKey;
import studio.craftory.core.data.safecontainers.SafeBlockLocation;
import studio.craftory.core.executors.AsyncExecutionManager;
import studio.craftory.core.executors.SyncExecutionManager;
import studio.craftory.core.utils.Log;

/** Class based on LogisticsCraft's Logistics-API (MIT) and the LogisticsTypeRegister class **/
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class CustomBlockRegister {

  @Inject
  private AsyncExecutionManager asyncExecutionManager;
  @Inject
  private SyncExecutionManager syncExecutionManager;

  private final Map<CustomBlockKey, Constructor<? extends BaseCustomBlock>> blockTypes = new HashMap<>();
  private final Map<Class<? extends BaseCustomBlock>, CustomBlockKey> blockKeys = new HashMap<>();
  private final Map<String, CraftoryDataKey> craftoryDataKeyMap = new HashMap<>();

  @Synchronized
  public void registerCustomBlock(@NonNull Plugin plugin, @NonNull Class<?> block) {
    CustomBlockKey customBlockKey = new CustomBlockKey(plugin, block);
    if (!blockTypes.containsKey(customBlockKey)) {

        Optional<Constructor<?>> constructor = getConstructor(block);
        if (constructor.isPresent()) {

          addCustomBlockKeys((Class<? extends BaseCustomBlock>) block, (Constructor<? extends BaseCustomBlock>) constructor.get(),customBlockKey);

          registerCustomBlockTickables((Class<? extends BaseCustomBlock>) block);
          Log.debug("CustomBlock Register: " + block.getName());
        } else {
          Log.warn("Couldn't get constructor for custom block: " + customBlockKey.getName());
        }

      //Bukkit.getPluginManager().callEvent(new BlockRegisterEvent(new CustomBlockKey(plugin, name), block));
    } else {
      Log.warn("Trying to re-register known key of Custom Block: " + customBlockKey.getName());
    }
  }

  @Synchronized
  public boolean isBlockRegistered(@NonNull BaseCustomBlock block) {
    return blockKeys.containsKey(block.getClass());
  }

  public Optional<BaseCustomBlock> getNewCustomBlockInstance(@NonNull CustomBlockKey key,  @NonNull Location location,
      @NonNull CraftoryDirection direction) {
    Optional<Constructor<? extends BaseCustomBlock>> constructor = Optional.ofNullable(blockTypes.get(key));

    if (constructor.isPresent()) {
      try {
        return Optional.of(constructor.get().newInstance(location, direction));
      } catch (Exception e) {
        Log.error("Couldn't create custom block of type: "+key.getName() + " at location: "+location);
        return Optional.empty();
      }
    }
    return Optional.empty();
  }

  @Synchronized
  public Optional<CustomBlockKey> getKey(@NonNull BaseCustomBlock block) {
    return getKey(block.getClass());
  }

  private Optional<Constructor<?>> getConstructor(@NonNull Class<?> clazz) {
    try {
      return Optional.of(clazz.getDeclaredConstructor(Location.class, CraftoryDirection.class));
    } catch(NoSuchMethodException e) {
      return Optional.empty();
    }
  }

  @Synchronized
  public Optional<CustomBlockKey> getKey(@NonNull Class<? extends BaseCustomBlock> block) {
    return Optional.ofNullable(blockKeys.get(block));
  }

  public void registerDataKey(String key, CraftoryDataKey dataKey) {
    craftoryDataKeyMap.putIfAbsent(key, dataKey);
  }

  public Optional<CraftoryDataKey> getDataType(@NonNull String key) {
    return Optional.of(craftoryDataKeyMap.get(key));
  }

  private void addCustomBlockKeys(Class<? extends BaseCustomBlock> clazz, Constructor<? extends BaseCustomBlock> constructor, CustomBlockKey key) {
    blockTypes.put(key, constructor);
    blockKeys.put(clazz, key);
  }

  private void registerCustomBlockTickables(Class<? extends BaseCustomBlock> block) {
    asyncExecutionManager.registerTickableClass(block);
    syncExecutionManager.registerTickableClass(block);
  }

}
