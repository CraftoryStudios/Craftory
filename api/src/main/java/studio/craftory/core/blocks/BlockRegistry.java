package studio.craftory.core.blocks;

import static studio.craftory.core.utils.Constants.Keys.BLOCK_ITEM_DATA_KEY;

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
import studio.craftory.core.containers.CraftoryDirection;
import studio.craftory.core.containers.keys.CraftoryBlockKey;
import studio.craftory.core.containers.keys.CraftoryDataKey;
import studio.craftory.core.executors.AsyncExecutionManager;
import studio.craftory.core.executors.SyncExecutionManager;
import studio.craftory.core.items.CustomItem;
import studio.craftory.core.utils.Log;

/** Class based on LogisticsCraft's Logistics-API (MIT) and the LogisticsTypeRegister class **/
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class BlockRegistry {

  @Inject
  private AsyncExecutionManager asyncExecutionManager;
  @Inject
  private SyncExecutionManager syncExecutionManager;

  private final Map<CraftoryBlockKey, Constructor<? extends CustomBlock>> blockTypes = new HashMap<>();
  private final Map<Class<? extends CustomBlock>, CraftoryBlockKey> blockKeys = new HashMap<>();
  //TODO Generate id's for blocks


  private final Map<String, CraftoryDataKey> craftoryDataKeyMap = new HashMap<>();
  private final Map<String, CraftoryBlockKey> craftoryBlockKeyMap = new HashMap<>();

  @Synchronized
  public CraftoryBlockKey registerCustomBlockClass(@NonNull Plugin plugin, @NonNull Class<?> block, @NonNull String model) {
    CraftoryBlockKey craftoryBlockKey = new CraftoryBlockKey(plugin, block);
    if (!blockTypes.containsKey(craftoryBlockKey)) {

        Optional<Constructor<?>> constructor = getConstructor(block);
        if (constructor.isPresent()) {

          addCustomBlockKeys((Class<? extends CustomBlock>) block, (Constructor<? extends CustomBlock>) constructor.get(), craftoryBlockKey);
          registerCustomBlockTickables((Class<? extends CustomBlock>) block);

          CustomItem.builder().name(craftoryBlockKey.getName()).displayName(
              craftoryBlockKey.getName()).material(Material.STONE).attribute(BLOCK_ITEM_DATA_KEY, craftoryBlockKey.toString()).material(
              Material.STONE).modelPath(model).build().register(plugin);
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
  public boolean isBlockClassRegistered(@NonNull CustomBlock block) {
    return blockKeys.containsKey(block.getClass());
  }

  public Optional<CustomBlock> getNewCustomBlockInstance(@NonNull CraftoryBlockKey key,  @NonNull Location location,
      @NonNull CraftoryDirection direction) {
    Optional<Constructor<? extends CustomBlock>> constructor = Optional.ofNullable(blockTypes.get(key));
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
  public Optional<CraftoryBlockKey> getBlockKey(@NonNull CustomBlock block) {
    return getBlockKey(block.getClass());
  }

  @Synchronized
  public Optional<CraftoryBlockKey> getBlockKey(@NonNull Class<? extends CustomBlock> block) {
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

  private void addCustomBlockKeys(Class<? extends CustomBlock> clazz, Constructor<? extends CustomBlock> constructor, CraftoryBlockKey key) {
    blockTypes.put(key, constructor);
    blockKeys.put(clazz, key);
    craftoryBlockKeyMap.put(key.toString(), key);
  }

  private void registerCustomBlockTickables(Class<? extends CustomBlock> block) {
    asyncExecutionManager.registerTickableClass(block);
    syncExecutionManager.registerTickableClass(block);
  }

}
