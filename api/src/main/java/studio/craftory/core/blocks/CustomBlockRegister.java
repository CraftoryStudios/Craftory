package studio.craftory.core.blocks;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import javax.inject.Inject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Synchronized;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import studio.craftory.core.blocks.templates.BaseCustomBlock;
import studio.craftory.core.data.CraftoryDirection;
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

  @Synchronized
  public void registerCustomBlock(@NonNull Plugin plugin, @NonNull Class<? extends BaseCustomBlock> block) {
    CustomBlockKey customBlockKey = new CustomBlockKey(plugin, block);
    if (!blockTypes.containsKey(customBlockKey)) {

        Optional<Constructor<? extends BaseCustomBlock>> constructor = getConstructor(block);
        if (constructor.isPresent()) {

          blockTypes.put(customBlockKey, constructor.get());

          registerCustomBlockTickables(block);
          Log.debug("CustomBlock Register: " + block.getName());
        } else {
          Log.warn("Couldn't get constructor for custom block: " + customBlockKey.getName());
        }

      //Bukkit.getPluginManager().callEvent(new BlockRegisterEvent(new CustomBlockKey(plugin, name), block));
    } else {
      Log.warn("Trying to re-register known key of Custom Block: " + customBlockKey.getName());
    }
  }

  private void registerCustomBlockTickables(Class<? extends BaseCustomBlock> block) {
    asyncExecutionManager.registerTickableClass(block);
    syncExecutionManager.registerTickableClass(block);
  }

  @Synchronized
  public boolean isBlockRegistered(@NonNull BaseCustomBlock block) {
    Optional<Constructor<? extends BaseCustomBlock>> constructor = getConstructor(block.getClass());
    return constructor.filter(blockTypes::containsValue).isPresent();
  }

  public Optional<BaseCustomBlock> getNewCustomBlockInstance(@NonNull CustomBlockKey key,  @NonNull Location location,
      @NonNull CraftoryDirection direction) {
    Optional<Constructor<? extends BaseCustomBlock>> constructor = Optional.ofNullable(blockTypes.get(key));

    if (constructor.isPresent()) {
      try {
        SafeBlockLocation safeLocation = new SafeBlockLocation(location);
        return Optional.of(constructor.get().newInstance(safeLocation, direction));
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

  private Optional<Constructor<? extends BaseCustomBlock>> getConstructor(@NonNull Class<? extends BaseCustomBlock> block) {
    try {
      return Optional.of(block.getDeclaredConstructor(SafeBlockLocation.class, CraftoryDirection.class));
    } catch(NoSuchMethodException e) {
      return Optional.empty();
    }
  }

  @Synchronized
  public Optional<CustomBlockKey> getKey(@NonNull Class<? extends BaseCustomBlock> block) {
    for (Entry<CustomBlockKey, Constructor<? extends BaseCustomBlock>> entry : blockTypes.entrySet()) {
      if (entry.getValue().getDeclaringClass() == block) {
        return Optional.of(entry.getKey());
      }
    }
    return Optional.empty();
  }
}
